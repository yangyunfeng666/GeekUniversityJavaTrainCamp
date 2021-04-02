package filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public interface HttpRequestFilter {
  void filter(HttpRequest request, ChannelHandlerContext channelHandlerContext);
}

