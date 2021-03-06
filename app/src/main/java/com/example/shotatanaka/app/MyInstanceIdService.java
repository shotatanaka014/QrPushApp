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
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyInstanceIdService extends FirebaseInstanceIdService {

    public static final String ACTION_TOKEN_REFRESHED	= "MyInstanceIdService.ACTION_TOKEN_REFRESHED";
    public static final String KEY_TOKEN				= "MyInstanceIdService.KEY_TOKEN";

    /**
     * 端末登録トークンが更新されたときに呼び出されるコールバック
     */
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        if (!TextUtils.isEmpty(token)) {
            android.util.Log.d("FCM-TEST", "token = " + token);

            // ブロードキャストレシーバーでActivityに制御を戻す
            Intent intent = new Intent();
            intent.setAction(ACTION_TOKEN_REFRESHED);
            intent.putExtra(KEY_TOKEN, token);
            getBaseContext().sendBroadcast(intent);
        }
    }
}