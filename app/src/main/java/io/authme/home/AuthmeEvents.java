package io.authme.home;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.authme.sdk.server.Callback;
import io.authme.sdk.server.Config;
import io.authme.sdk.server.PostData;

/**
 * Created by shardullavekar on 26/03/17.
 */

public class AuthmeEvents {

    public static void logEvent(String orderId, String eventName, String emailId, String otp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Email", emailId);
            jsonObject.put("Otp", otp);
            jsonObject.put("ReferenceId", orderId);
            jsonObject.put("Event", eventName);

        } catch (JSONException e) {
            return;
        }

        try {
            new PostData(new Callback() {
                @Override
                public void onTaskExecuted(String response) {

                }
            }, "k-50aa7bbe-d669-4cf3-b7f3-7272e9d9d926").runPost(Config.PROD_SERVER_URL + "event/order", jsonObject.toString());
        } catch (IOException e) {
            return;
        }

    }
}
