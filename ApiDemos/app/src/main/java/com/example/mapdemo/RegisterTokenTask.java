package com.example.mapdemo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by fabiomelo on 12/11/16.
 */

class RegisterTokenTask extends AsyncTask<String, Void, String> {

    private OkHttpClient mClient = new OkHttpClient();

    protected String doInBackground(String... json) {
        try {
            return registerToken(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String registerToken(String... token) throws IOException {
        if (token == null || token.length == 0 || token[0] == null) {
            return null;
        }
        RequestBody body = RequestBody.create(Constants.JSON, "\"" + token[0] + "\"");
        Request request = new Request.Builder()
                .post(body)
                .url("http://convoynet.azurewebsites.net/api/Id/")
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        Log.i("Convoy register", "#####  " + aVoid + " #####");
    }
}