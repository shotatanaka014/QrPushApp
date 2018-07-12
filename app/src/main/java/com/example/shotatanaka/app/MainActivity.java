package com.example.shotatanaka.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {

    // FirebaseInstanceIdServiceやFirebaseMessagingServiceから送信される
    // ブロードキャストを受けてUIに反映させるBroadcastReceiver
    private class MessagingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }

            switch (action) {
                case MyInstanceIdService.ACTION_TOKEN_REFRESHED:
                    String token = intent.getStringExtra(MyInstanceIdService.KEY_TOKEN);
                    break;
                case MyMessagingService.ACTION_MESSAGE_RECEIVED:
                    String message = intent.getStringExtra(MyMessagingService.KEY_MESSAGE);
                    TextView Notification2 = (TextView)findViewById(R.id.Notification);
                    Notification2.setText(message);
                    break;
            }
        }
    }

    private MessagingReceiver mReceiver;

    // トークンの取得が行われたことがあるかどうか
    // 取得はonCreateでやってもいいが、確実にregisterReceiverされたあとに呼び出したいので
    private boolean mIsTokenRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new MessagingReceiver();

        // localDBから自分と友達の名前を取得する
        MySqliteHelper helper = new MySqliteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM users;";
        Cursor c = db.rawQuery(sql,null);

        // 友達リスト
        ArrayList friendList = new ArrayList<>();
        ListView listView = (ListView)findViewById(R.id.friendList);

        boolean isExistMyName = false;
        boolean isEof = c.moveToFirst();
        while (isEof){
            if(c.getInt(2) == 1){
                // 自分自身のデータ
                TextView name = (TextView)findViewById(R.id.NameEdit);
                name.setText(c.getString(0));
                isExistMyName = true;
            }else{
                // 友達のデータ
                friendList.add(c.getString(0)+"　"+c.getString(1));
            }
            isEof = c.moveToNext();
        }
        c.close();

        // ListViewに友達リストを詰める
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, friendList);
        listView.setAdapter(adapter);

        // 自分の名前が登録されていない場合「名無しさん」で登録しちゃう
        if(!isExistMyName){
            // 自分の名前をlocalDBに保存する
            SQLiteDatabase wDb  = helper.getWritableDatabase();
            String insertSql = "INSERT INTO users VALUES ('名無しさん',null,1);";
            wDb.execSQL(insertSql);
        }

        // QRコードを生成する画面へ遷移
        Button QrWriteButton = (Button)findViewById(R.id.QrWrite);
        QrWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), QrWriteActivity.class);
                startActivity(intent);
            }
        });

        // QRコードを読み取るカメラを起動
        Button QrReadButton = (Button)findViewById(R.id.QrRead);
        QrReadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IntentIntegrator(MainActivity.this).initiateScan();
            }
        });

        // 名前を編集
        Button NameUpdateButton = (Button)findViewById(R.id.NameUpdate);
        NameUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText name = (EditText)findViewById(R.id.NameEdit);

                // 自分の名前をlocalDBに保存する
                MySqliteHelper helper = new MySqliteHelper(getApplication());
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "UPDATE users SET name='"+name.getText().toString()+"' WHERE isOwner=1;";
                db.execSQL(sql);
            }
        });

        //リスト項目が選択された時のイベントを追加
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView)parent;
                String userData = (String)listView.getItemAtPosition(position);
                Intent intent = new Intent(getApplication(), FriendChatActivity.class);
                intent.putExtra("name", userData);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                // 相手の名前とtokenIdをlocalDBに保存する
                MySqliteHelper helper = new MySqliteHelper(this);
                SQLiteDatabase wDb  = helper.getWritableDatabase();
                String[] nameAndTokenId = result.getContents().split(",", 0);
                String insertSql = "INSERT INTO users VALUES ('"+nameAndTokenId[0]+"','"+nameAndTokenId[1]+"',0);";
                wDb.execSQL(insertSql);
                Toast.makeText(this, nameAndTokenId[0]+"さんを登録しました", Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyInstanceIdService.ACTION_TOKEN_REFRESHED);
        filter.addAction(MyMessagingService.ACTION_MESSAGE_RECEIVED);
        registerReceiver(mReceiver, filter);

        if (!mIsTokenRequested) {
            String token = FirebaseInstanceId.getInstance().getToken();
            if (!TextUtils.isEmpty(token)) {

                TextView name = (TextView)findViewById(R.id.TokenId);
                name.setText(token);

                // トークンIDを保存
                MySqliteHelper helper = new MySqliteHelper(getApplication());
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "UPDATE users SET fcmTokenId='"+token+"' WHERE isOwner=1;";
                db.execSQL(sql);
            } else {
                // トークン取得失敗
            }
            mIsTokenRequested = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
    }
}
