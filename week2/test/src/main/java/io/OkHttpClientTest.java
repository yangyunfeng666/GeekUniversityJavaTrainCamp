package io;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public class OkHttpClientTest {

  private static final String URL = "http://localhost:8080";

  public static void main(String[] args) {
    okhttp3.OkHttpClient okHttpClient = new OkHttpClient();
    final Request request = new Request.Builder().url(URL).get().build();
    Call call = okHttpClient.newCall(request);
    call.enqueue(new Callback() {
      public void onFailure(@NotNull Call call, @NotNull IOException e) {
        System.out.println(e);
        call.clone();
      }

      public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        System.out.println(response.body().string());
        call.clone();
      }
    });
  }

}
