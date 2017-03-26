package io.authme.home;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.authme.sdk.server.Callback;
import io.authme.sdk.server.Config;
import io.authme.sdk.server.PostData;


/**
 * Created by parth on 14/2/17.
 */
public class AuthmeEvents {

    private static final String AUTHMEIO = "AUTHMEIO";

    public static void EventOrder(String orderId, String eventName, String emailId, String otp, String apiKey) {
        JSONObject gcmToken = new JSONObject();
        try {
            gcmToken.put("Email", emailId);
            gcmToken.put("Otp", otp);
            gcmToken.put("ReferenceId", orderId);
            gcmToken.put("Event", eventName);

        } catch (JSONException e) {
            Log.d(AUTHMEIO, "Failed to make json", e);
        }

        try {
            new PostData(new Callback() {
                @Override
                public void onTaskExecuted(String response) {
                    Log.d(AUTHMEIO, "Ack complete");
                }
            }, apiKey).runPost(Config.PROD_SERVER_URL + "event/order", gcmToken.toString());
        } catch (IOException e) {
            Log.d(AUTHMEIO, "Failed to ack order", e);
        }
    }
}
