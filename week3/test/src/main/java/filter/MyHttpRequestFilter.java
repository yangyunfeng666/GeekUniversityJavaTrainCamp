package filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class MyHttpRequestFilter implements HttpRequestFilter {

  @Override
  public void filter(HttpRequest request, ChannelHandlerContext channelHandlerContext) {
    //在请求的头里面添加author属性，aa
    request.headers().set("author","aaa");
  }
}
