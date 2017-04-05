//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.authme.home;

import android.os.AsyncTask;
import android.util.Log;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.Request.Builder;
import io.authme.sdk.server.Callback;
import io.authme.sdk.server.PostRequest;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MarkNotMine {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String AUTHMEIO = "AUTHMEIO";
    private static final String API_KEY_HEADER = "X-Api-Key";
    OkHttpClient client;
    Callback callback;
    String apiKey;

    public MarkNotMine(Callback callback, String apiKey, String email) {
        this.callback = callback;
        this.client = new OkHttpClient();
        this.client.setConnectTimeout(5L, TimeUnit.MINUTES);
        this.client.setReadTimeout(5L, TimeUnit.MINUTES);
        this.client.setWriteTimeout(5L, TimeUnit.MINUTES);
        this.apiKey = apiKey;
    }

    public void runPost(String url, String json) throws IOException {
        this.post(url, json);
    }

    void post(String url, String json) throws IOException {
        MarkNotMine.PostRequestExecutor postRequestExecutor = new MarkNotMine.PostRequestExecutor();
        PostRequest post = new PostRequest(url, "POST");
        post.setBody(json);
        postRequestExecutor.execute(new PostRequest[]{post});
    }

    class PostRequestExecutor extends AsyncTask<PostRequest, String, String> {
        PostRequestExecutor() {
        }

        protected String doInBackground(PostRequest... uri) {
            RequestBody body = RequestBody.create(MarkNotMine.JSON, uri[0].getBody().getBytes());
            Request request = (new Builder()).url(uri[0].getUrl()).addHeader("X-Api-Key", MarkNotMine.this.apiKey).post(body).build();
            Response response = null;

            try {
                response = MarkNotMine.this.client.newCall(request).execute();
            } catch (IOException var7) {
                Log.e("AUTHMEIO", "Failed to execute post: ", var7);
                return "";
            }

            try {
                return response.body().string();
            } catch (IOException var6) {
                Log.e("AUTHMEIO", "Failed to get body post: ", var6);
                return "";
            }
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MarkNotMine.this.callback.onTaskExecuted(s);
        }
    }
}
