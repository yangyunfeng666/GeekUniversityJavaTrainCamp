package netty;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

public class HttpOutboundHandler {

  private OkHttpClient okHttp;


  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private static final MediaType FORM = MediaType
      .parse("application/x-www-form-urlencoded; charset=utf-8");

  public HttpOutboundHandler() {
    this.okHttp = new OkHttpClient();
  }

  public void flush(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx,
      String url) {
    System.out.println("the url:" + url);
    if (fullHttpRequest.method() == HttpMethod.GET) {
      final Request request = new Request.Builder().url(url).get().build();
      okHttp.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
          sendResponse(fullHttpRequest, ctx, e.getMessage());
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
          sendResponse(fullHttpRequest, ctx, response.body().string());
        }
      });
    } else if (fullHttpRequest.method() == HttpMethod.POST) {
      RequestBody requestBody = null;
      String contentType = fullHttpRequest.headers().get("Content-Type").trim().toLowerCase();
      Map<String, Object> param = HttpRequestParser.readPostParams(fullHttpRequest);
      if (contentType.contains("x-www-form-urlencoded")) {
        requestBody = RequestBody.create(FORM, JSONObject.toJSONString(param));
      } else if (contentType.contains("json")) {
        requestBody = RequestBody.create(JSON, JSONObject.toJSONString(param));
      }

      final Request request = new Request.Builder().url(url).post(requestBody).build();
      okHttp.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
          sendResponse(fullHttpRequest, ctx, "post 请求");
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
          sendResponse(fullHttpRequest, ctx, response.body().string());
        }
      });
    }

  }

  public void sendResponse(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx,
      String body) {
    FullHttpResponse response = null;
    try {
      response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
          Unpooled.wrappedBuffer(body.getBytes()));
      response.headers().set("Content-Type", "application/json");
      response.headers().setInt("Content-Length", response.content().readableBytes());
    } catch (Exception e) {
      response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
    } finally {
      if (response != null) {
        if (!HttpUtil.isKeepAlive(fullHttpRequest)) {
          ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
          response.headers().set("CONNECTION", "KEEP_ALIVE");
          ctx.write(response);
        }
      }
      ctx.flush();
    }
  }


}
