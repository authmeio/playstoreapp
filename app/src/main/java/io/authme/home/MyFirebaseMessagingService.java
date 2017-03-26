package io.authme.home;

import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import io.authme.sdk.AuthScreen;
import io.authme.sdk.server.Config;

/**
 * Created by shardullavekar on 26/03/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (!remoteMessage.getData().isEmpty()) {
            Config config = new Config(getApplicationContext());

            String command = remoteMessage.getData().get("Command");

            if (TextUtils.equals(command, "InitiateAuth")) {
                String orderId = remoteMessage.getData().get("OrderId");
                Intent intent = new Intent("LOCKPATTERN");
                intent.setClassName(getApplicationContext(), AuthScreen.class.getCanonicalName());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("referenceId", orderId);
                intent.putExtra("email", config.getEmailId());
                AuthmeEvents.logEvent(orderId, "ORDER_ACK", config.getEmailId(), config.getOTP());
                startActivity(LandingPage.addOns(intent));
                return;
            }

            if (TextUtils.equals(command, "OtpSecret")) {
                String data = remoteMessage.getData().get("Data");
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    config.setSecretKey(jsonObject.getString("OTP_SECRET"));
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            }

        }

    }
}
