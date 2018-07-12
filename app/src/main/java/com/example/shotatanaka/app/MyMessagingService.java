/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.shotatanaka.app;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Locale;
import java.util.Map;

public class MyMessagingService extends FirebaseMessagingService {

    public static final String ACTION_MESSAGE_RECEIVED	= "MyMessagingService.ACTION_MESSAGE_RECEIVED";
    public static final String KEY_MESSAGE				= "MyMessagingService.KEY_MESSAGE";

    /**
     * FCMメッセージを受信した際に呼び出されるコールバック
     * @param remoteMessage	FCMメッセージを保持するオブジェクト
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // 通知メッセージの受信
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            String title = notification.getTitle();
            String body = notification.getBody();

            sendMessage("通知", title, body);

            return;
        }

        // データメッセージの受信
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            String subject = data.get("subject");
            String text = data.get("text");

            sendMessage("データ", subject, text);
        }
    }

    /**
     * FCMメッセージを受信したらMainActivityのレシーバーに送る
     */
    private void sendMessage(String type, String title, String text) {
        //String message = String.format(Locale.getDefault(), "メッセージタイプ: %1$s\nタイトル: %2$s\n本文: %3$s", type, title, text);
        Intent intent = new Intent();
        intent.setAction(ACTION_MESSAGE_RECEIVED);
        intent.putExtra(KEY_MESSAGE, text);
        getBaseContext().sendBroadcast(intent);
    }
}