package io.authme.home;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.authme.sdk.server.Config;
import io.authme.sdk.server.PostData;
import io.authme.sdk.server.Callback;

/**
 * Created by shardullavekar on 26/03/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken, getApplicationContext());
    }

    public static void sendRegistrationToServer(final String token, Context context) {
        Config config; final App app;

        config = new Config(context);
        app = new App(context);

        if (TextUtils.isEmpty(config.getEmailId())) {
            return;
        }

        JSONObject gcmToken = new JSONObject();
        try {
            gcmToken.put("Email", config.getEmailId());
            gcmToken.put("Provider", "fcm");
            gcmToken.put("Platform", "android");
            gcmToken.put("PackageName", context.getPackageName());
            gcmToken.put("Token", token);
            gcmToken.put("Otp", config.getOTP());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            new PostData(new Callback() {
                @Override
                public void onTaskExecuted(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (TextUtils.equals(jsonObject.getString("Status"), "200")) {
                            app.setGCMToken(token);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, "k-50aa7bbe-d669-4cf3-b7f3-7272e9d9d926").runPost(config.getServerURL() + "messengertoken/update", gcmToken.toString());
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
