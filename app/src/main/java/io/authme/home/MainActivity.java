package io.authme.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.authme.sdk.AuthScreen;
import io.authme.sdk.server.Callback;
import io.authme.sdk.server.Config;
import io.authme.sdk.server.PostData;

import static io.authme.home.LandingPage.RESULT;
import static io.authme.home.R.id.trustlayout;

public class MainActivity extends AppCompatActivity {
    Config config;

    Button button;

    RelativeLayout trust;

    TextView signature, speed, motion;

    String email;
    private int differentUserId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = getIntent().getStringExtra("email");

        trust = (RelativeLayout) this.findViewById(trustlayout);

        config = new Config(this.getApplicationContext());

        if (TextUtils.isEmpty(config.getEmailId())) {
            config.setEmailId(email);
        }
        button = (Button) this.findViewById(R.id.train);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trust.setVisibility(View.GONE);

                Intent intent = new Intent(MainActivity.this, AuthScreen.class);

                intent.putExtra("titlecolor", "#433f5b");

                intent.putExtra("statusbar", "#544e6b");

                intent.putExtra("email", email);

                startActivityForResult(intent, RESULT);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT: {
                switch (resultCode) {
                    case Config.SIGNUP_PATTERN: {

                    }
                    break;

                    case Config.LOGIN_PATTERN: {
                        showScores(data.getStringExtra("response"));
                    }
                    break;

                    case Config.RESET_PATTERN: {

                    }
                    break;

                    case Config.RESULT_FAILED: {
                        Toast.makeText(getApplicationContext(), "Failed To Identify", Toast.LENGTH_LONG)
                                .show();
                        if (data.hasExtra("response")) {
                            Toast.makeText(getApplicationContext(), data.getStringExtra("response"), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                    break;

                    default:
                        break;
                }


                if (resultCode == Config.LOGIN_PATTERN || resultCode == Config.RESULT_FAILED) {
                    if (this.differentUserId > 0) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("User", config.getEmailId());
                            jsonObject.put("DifferentUser", this.differentUserId);

                            new PostData(new Callback() {
                                @Override
                                public void onTaskExecuted(String s) {

                                }
                            }, Config.API_KEY).runPost(Config.PROD_SERVER_URL + "api/notmine", jsonObject.toString());
                        } catch (JSONException | IOException e) {
                            Log.e("AUTHMEIO", "Failed to mark as not mine", e);
                        }
                    }
                }

            }
            break;

            default:
                break;

        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_activity_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
////            case R.id.action_settings:
////                // User chose the "Settings" item, show the app settings UI...
////                return true;
////
////
////            case R.id.action_not_me:
////                this.differentUserId += 1;
////                return true;
////
////
////            case R.id.action_me:
////                this.differentUserId = 0;
////                return true;
////
////
////            default:
////                // If we got here, the user's action was not recognized.
////                // Invoke the superclass to handle it.
////                return super.onOptionsItemSelected(item);
//
//        }
//    }


    private void showScores(String response) {

        try {
            JSONObject jsonScore = new JSONObject(response);

            if (jsonScore.has("Speed") && jsonScore.has("Motion") && jsonScore.has("Path")) {

                trust.setVisibility(View.VISIBLE);

                RelativeLayout speedLayout = (RelativeLayout) this.findViewById(R.id.trustlayout);

                if (jsonScore.getBoolean("Accept")) {
                    speedLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                else {
                    speedLayout.setBackgroundColor(getResources().getColor(R.color.colorReject));
                }

                speed = (TextView) this.findViewById(R.id.speedscore);

                motion = (TextView) this.findViewById(R.id.motionscore);

                signature = (TextView) this.findViewById(R.id.signaturescore);

                speed.setText(jsonScore.getString("Speed") + "% match");

                motion.setText(jsonScore.getString("Motion") + "% match");

                signature.setText(jsonScore.getString("Path") + "% match");
            }
            
            else if (jsonScore.has("Reason")) {
                Log.d("Reason", jsonScore.getString("Reason"));

                if (TextUtils.equals(jsonScore.getString("Reason"), "Pin Verification")) {
                    Toast.makeText(getApplicationContext(), "Pin Verified",
                            Toast.LENGTH_LONG)
                            .show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Swipe at least 5 times",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Swipe at least 5 times for trust scores", Toast.LENGTH_LONG)
                    .show();
            trust.setVisibility(View.GONE);
            return;
        }

    }
}
