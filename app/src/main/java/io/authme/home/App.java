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

    /**
     * Error:Execution failed for task ':app:clean'.
     > Unable to delete directory: /media/artpar/ddrive/workspace/code/playstoreapp/app/build/intermediates/exploded-aar/com.android.support/appcompat-v7/25.1.0/jars
     * @param token
     */

    public void setGCMToken(String token) {
        this.editor.putString(GCM, token);
        this.editor.apply();
        this.editor.commit();
    }

    public String getGCMToken() {
        return sharedPreferences.getString(GCM, null);
    }

}
