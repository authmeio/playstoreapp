package io.authme.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.authme.sdk.AuthScreen;
import io.authme.sdk.server.Config;

import static io.authme.home.LandingPage.RESULT;
import static io.authme.home.R.id.trustlayout;

public class MainActivity extends AppCompatActivity {
    Config config;

    Button button;

    RelativeLayout trust;

    TextView signature, speed, motion;

    String email;

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

            }
            break;

            default:
                break;

        }
    }

    private void showScores(String response) {
        trust.setVisibility(View.VISIBLE);

        RelativeLayout speedLayout = (RelativeLayout) this.findViewById(R.id.trustlayout);
        speed = (TextView) this.findViewById(R.id.speedscore);

        motion = (TextView) this.findViewById(R.id.motionscore);

        signature = (TextView) this.findViewById(R.id.signaturescore);

        try {
            JSONObject jsonScore = new JSONObject(response);

            speed.setText(jsonScore.getString("Speed") + "% match");

            motion.setText(jsonScore.getString("Motion") + "% match");

            signature.setText(jsonScore.getString("Path") + "% match");

            if ("true".equalsIgnoreCase(jsonScore.getString("Accept"))) {
                speedLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                speedLayout.setBackgroundColor(getResources().getColor(R.color.colorReject));
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
