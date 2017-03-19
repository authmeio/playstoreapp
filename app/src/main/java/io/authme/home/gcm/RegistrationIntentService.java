package io.authme.home.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.authme.home.App;
import io.authme.sdk.server.Callback;
import io.authme.sdk.server.Config;
import io.authme.sdk.server.PostData;


/**
 * Created by shardul on 22-04-2016.
 */
public class RegistrationIntentService extends IntentService {
    public static final String GCM_TOKEN = "gcmToken";

    private static final String TAG = "RegIntentService";

    Config config; App app;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Make a call to Instance API
        InstanceID instanceID = InstanceID.getInstance(this);
        String senderId = "720738612328";

        config = new Config(getApplicationContext());
        app = new App(getApplicationContext());

        try {
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.
        JSONObject gcmToken = new JSONObject();
        try {
            gcmToken.put("Email", config.getEmailId());
            gcmToken.put("Key", "gcm_token");
            gcmToken.put("Provider", "gcm");
            gcmToken.put("Platform", "android");
            gcmToken.put("PackageName", this.getApplicationContext().getPackageName());
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
