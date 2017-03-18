package io.authme.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.authme.sdk.AuthScreen;
import io.authme.sdk.server.Config;

public class LandingPage extends AppCompatActivity {
    Button signup, qrcode;
    EditText editText;
    Config config;
    TextView error;
    String email;

    public final static int RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        config = new Config(LandingPage.this);

        if (config.isValidConfig()) {
            LandingPage.this.finish();
            startMainactivity();
            return;
        }

        signup = (Button) this.findViewById(R.id.signup);

        qrcode = (Button) this.findViewById(R.id.qrcode);

        editText = (EditText) this.findViewById(R.id.emailId);

        error = (TextView) this.findViewById(R.id.error);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editText.getText().toString();

                if (!Config.isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                else {
                    config.setEnvironment(Config.PRODUCTION);

                    config.setAPIKey("k-862b77f3-5937-4856-af1b-8950a001f733");

                    Intent intent = new Intent(LandingPage.this, AuthScreen.class);

                    intent.putExtra("email", email);

                    startActivityForResult(intent, RESULT);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT : {
                switch (resultCode) {
                    case Config.SIGNUP_PATTERN : {
                        startMainactivity();
                        this.finish();
                    } break;

                    case Config.LOGIN_PATTERN : {
                        Toast.makeText(getApplicationContext(), data.getStringExtra("response"), Toast.LENGTH_LONG)
                                .show(); //you will get a trust score in the response here.
                    } break;

                    case Config.RESET_PATTERN: {
                        error.setText(R.string.error);
                        hidenSeek();
                    } break;

                    case Config.RESULT_FAILED : {
                        Toast.makeText(getApplicationContext(), "Failed To Identify", Toast.LENGTH_LONG)
                                .show();
                        if (data.hasExtra("response")) {
                            Toast.makeText(getApplicationContext(), data.getStringExtra("response"), Toast.LENGTH_LONG)
                                    .show();
                        }
                    } break;

                    default: break;
                }

            } break;

            default: break;
        }
    }

    private void hidenSeek() {
        editText.setVisibility(View.GONE);

        signup.setVisibility(View.GONE);

        qrcode.setVisibility(View.VISIBLE);

        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //implement the qr code here
            }
        });
    }

    private void startMainactivity() {
        Intent intent = new Intent(LandingPage.this, MainActivity.class);
        email = config.getEmailId();
        intent.putExtra("email", email);
        startActivity(intent);
    }
}
