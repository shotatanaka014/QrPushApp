package com.example.shotatanaka.app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;


public class QrWriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrwrite);

        // localDBから自分の名前を取得する
        MySqliteHelper helper = new MySqliteHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM users WHERE isOwner=1;";
        Cursor c = db.rawQuery(sql,null);
        c.moveToFirst();

        // QRコード化する文字列
        String data = c.getString(0);
        data += ","+c.getString(1);
        int size = 500;

        try{
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

            //日本語が使えるようにエンコード
            //生成処理
            ConcurrentHashMap hints = new ConcurrentHashMap();
            //エラー訂正レベル指定
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            //エンコーディング指定
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            //マージン指定
            hints.put(EncodeHintType.MARGIN, 0);
            //QRコードをBitmapで作成
            Bitmap bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, size, size, hints);

            //作成したQRコードを画面上に配置
            ImageView imageViewQrCode = (ImageView) findViewById(R.id.qrImage);
            imageViewQrCode.setImageBitmap(bitmap);

        }catch(WriterException e){
            throw new AndroidRuntimeException("Barcode Error", e);
        }
    }
}
