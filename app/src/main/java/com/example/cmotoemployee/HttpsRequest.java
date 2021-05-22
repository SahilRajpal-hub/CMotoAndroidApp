package com.example.cmotoemployee;

import com.google.firebase.perf.network.FirebasePerfOkHttpClient;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;

class HttpsRequest {
    OkHttpClient client = new OkHttpClient();

    String run(String paramString) throws IOException {
        Request request = (new Request.Builder()).url(paramString).build();
        return FirebasePerfOkHttpClient.execute(this.client.newCall(request)).body().string();
    }
}