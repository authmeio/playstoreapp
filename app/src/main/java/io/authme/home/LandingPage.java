package io.authme.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

import io.authme.home.gcm.RegistrationIntentService;
import io.authme.sdk.AuthScreen;
import io.authme.sdk.server.Config;

public class LandingPage extends AppCompatActivity {
    Button signup, qrcode;
    EditText editText;
    Config config;
    TextView error;
    String email;
    App app;

    public final static int RESULT = 1, MY_PERMISSIONS_REQUEST_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        app = new App(getApplicationContext());

        config = new Config(getApplicationContext());

        config.setEnvironment(Config.PRODUCTION);

        config.setAPIKey("k-862b77f3-5937-4856-af1b-8950a001f733");

        if (!TextUtils.isEmpty(config.getEmailId())) {
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
                email = editText.getText().toString().trim();

                if (!Config.isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_LONG)
                            .show();
                    return;
                } else {
                    Intent intent = new Intent(LandingPage.this, AuthScreen.class);

                    intent.putExtra("email", email);

                    startActivityForResult(addOns(intent), RESULT);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT: {
                switch (resultCode) {
                    case Config.SIGNUP_PATTERN: {
                        initPush();
                    }
                    break;

                    case Config.LOGIN_PATTERN: {
                        Toast.makeText(getApplicationContext(), data.getStringExtra("response"), Toast.LENGTH_LONG)
                                .show(); //you will get a trust score in the response here.
                    }
                    break;

                    case Config.RESET_PATTERN: {
                        error.setText(R.string.error_account_exists);
                        hidenSeek();
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

            case MY_PERMISSIONS_REQUEST_CAMERA: {

            }
            break;

            default:
                break;
        }
    }

    private void hidenSeek() {
        editText.setVisibility(View.INVISIBLE);

        signup.setVisibility(View.INVISIBLE);

        qrcode.setVisibility(View.VISIBLE);

        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    askPermission();
                } else {
                    scanQRcode();
                }
            }
        });
    }

    private void startMainactivity() {
        Intent intent = new Intent(LandingPage.this, MainActivity.class);
        email = config.getEmailId();
        intent.putExtra("email", email);
        startActivity(intent);
        this.finish();
    }

    private void initPush() {
        startService(new Intent(getApplicationContext(), RegistrationIntentService.class));
        startMainactivity();
    }

    private Intent addOns(Intent intent) {
        intent.putExtra("titlecolor", "#433f5b");
        intent.putExtra("statusbar", "#544e6b");
        return intent;
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(LandingPage.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingPage.this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(LandingPage.this, "We need camera to scan QR code", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(LandingPage.this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            scanQRcode();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scanQRcode();
                } else {
                    Toast.makeText(getApplicationContext(), "We can't sync your device with browser without camera permision", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            }
            default:
                break;

        }
    }

    /**
     * Error:FAILURE: Build failed with an exception.

     * What went wrong:
     Execution failed for task ':app:clean'.
     > Unable to delete directory: /media/artpar/ddrive/workspace/code/playstoreapp/app/build/intermediates/exploded-aar/com.android.support/animated-vector-drawable/25.1.0/jars

     * Try:
     Run with --stacktrace option to get the stack trace. Run with --debug option to get more log output.

     */

    private void scanQRcode() {
        MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(LandingPage.this)
                .withEnableAutoFocus(true)
                .withCenterTracker()
                .withOnlyQRCodeScanning()
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        if (TextUtils.isEmpty(barcode.rawValue)) {
                            return;
                        }
                        Intent intent = new Intent(LandingPage.this, AuthScreen.class);
                        intent.putExtra("email", email);
                        intent.putExtra("resetKey", barcode.rawValue);
                        startActivityForResult(addOns(intent), RESULT);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }
}
