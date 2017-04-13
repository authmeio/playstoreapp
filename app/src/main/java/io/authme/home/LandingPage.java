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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.authme.home.history.PendingLogins;
import io.authme.sdk.AuthScreen;
import io.authme.sdk.server.Callback;
import io.authme.sdk.server.Config;

public class LandingPage extends AppCompatActivity {
    Button signup, qrcode;
    EditText editText;
    Config config;
    TextView error;
    String email;
    App app;
    FirebaseInstanceId firebaseInstanceId;

    public final static int RESULT = 1, MY_PERMISSIONS_REQUEST_CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        app = new App(getApplicationContext());

        config = new Config(getApplicationContext());

        config.setEnvironment(Config.PRODUCTION);

        //config.setAPIKey("k-c7f4a6b6-cd4b-4985-baa1-c18e74cb7d50");

        config.setAPIKey("k-50aa7bbe-d669-4cf3-b7f3-7272e9d9d926");

        checkPendingLogins();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT: {
                switch (resultCode) {
                    case Config.SIGNUP_PATTERN: {
                        startMainactivity();
                    }
                    break;

                    case Config.LOGIN_PATTERN: {
                        try {
                            JSONObject jsonObject = new JSONObject(data.getStringExtra("response"));
                            if (jsonObject.getBoolean("Accept")) {
                                startMainactivity();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Incorrect Details", Toast.LENGTH_LONG)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

        if (TextUtils.isEmpty(app.getGCMToken())) {
            firebaseInstanceId = FirebaseInstanceId.getInstance();
            String token = firebaseInstanceId.getToken();
            MyFirebaseInstanceIDService.sendRegistrationToServer(token, getApplicationContext());
        }

        startActivity(intent);
        this.finish();
    }

    public static final Intent addOns(Intent intent) {
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

    private void checkPendingLogins() {
        Callback callback = new Callback() {
            @Override
            public void onTaskExecuted(String s) {
                try {
                    JSONObject pendingObject = new JSONObject(s);
                    if (TextUtils.equals(pendingObject.getString("Status"), "ok")) {
                        JSONArray pendingArray = pendingObject.getJSONArray("Data");
                        if (pendingArray.length() > 0) {
                            Intent intent = new Intent(LandingPage.this, PendingLogins.class);
                            intent.putExtra("pending", pendingArray.toString());
                            startActivity(intent);
                        }
                        else {
                            loadLandingPage();
                        }
                    }
                    else {
                        loadLandingPage();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        config.getPendingLogins(callback);
    }

    private void loadLandingPage() {

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
}
