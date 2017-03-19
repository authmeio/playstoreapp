package io.authme.home;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shardullavekar on 19/03/17.
 */

public class App {
    Context context;
    public static final String APP_VALUES = "APP_VALUES",
            GCM = "GCM";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public App (Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(APP_VALUES, 0);
        editor = sharedPreferences.edit();
    }

    public void setGCMToken(String token) {
        this.editor.putString(GCM, token);
        this.editor.apply();
        this.editor.commit();
    }

    public String getGCMToken() {
        return sharedPreferences.getString(GCM, null);
    }

}
