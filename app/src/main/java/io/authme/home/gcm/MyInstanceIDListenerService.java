package io.authme.home.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by shardul on 22-04-2016.
 */
public class MyInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {

        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
