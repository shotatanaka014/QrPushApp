package com.example.shotatanaka.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * 友達一覧から遷移するチャット画面
 */
public class FriendChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendchat);

        TextView name = (TextView)findViewById(R.id.FriendName);
        Intent intent = getIntent();
        String data = intent.getStringExtra("name");
        name.setText(data);
    }
}
