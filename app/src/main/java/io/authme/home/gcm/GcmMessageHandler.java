package io.authme.home.gcm;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import io.authme.home.App;
import io.authme.sdk.AuthScreen;
import io.authme.sdk.server.Config;

/**
 * Created by shardul on 22-04-2016.
 */
public class GcmMessageHandler extends GcmListenerService {

    private static final String AUTHMEIO = "AUTHMEIO";
    App app;

    Config config;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.toString();
        app = new App(getApplicationContext());
        config = new Config(getApplicationContext());
        int first = message.indexOf("{");
        int second = message.indexOf("{", first + 1);
        if (second != -1) {
            String message_trunked = message.substring(second, message.length());
            int thelastComma = message_trunked.lastIndexOf(",");
            String jsonStr = message_trunked.substring(0, thelastComma);

            JSONObject jsonObject;
            String orderId = null, publicKeyJson = null;
            try {
                jsonObject = new JSONObject(jsonStr);

                if ("OtpSecret".equals(jsonObject.getString("Command"))) {
                    String secret = jsonObject.getJSONObject("Data").getString("OTP_SECRET");
                    config.setSecretKey(secret);
                    return;
                }
                orderId = jsonObject.getString("OrderId");

            } catch (JSONException e) {
                return;
            }

            Intent intent = new Intent("LOCKPATTERN");
            intent.setClassName(getApplicationContext(), AuthScreen.class.getCanonicalName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("referenceId", orderId);
            intent.putExtra("email", config.getEmailId());
            startActivity(intent);
        } else {

        }

    }


}
