package com.example.mapdemo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.mapdemo.Constants.JSON;

/**
 * Created by fabiomelo on 12/11/16.
 */

class UpdatePositionTask extends AsyncTask<String, Void, Void> {

    private OkHttpClient mClient = new OkHttpClient();

    protected Void doInBackground(String... json) {
        try {
            updatePosition(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String updatePosition(String[] json) throws IOException {
        String reqBody = "\"" + json[0].replace("\"", "\\\"").replace("\\\"true\\\"", "true").replace("\\\"false\\\"", "false") + "\"";
        RequestBody body = RequestBody.create(JSON, reqBody);
        Request request = new Request.Builder()
                .post(body)
                .url("http://convoynet.azurewebsites.net/api/Position/")
                .build();
        Response response = mClient.newCall(request).execute();
        Log.i("Convoy update", reqBody);
        return response.body().string();
    }


}