package netty;

import filter.HttpResposeFilter;
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
import java.util.ArrayList;
import java.util.List;

public class HttpClientOutboundHandler {

  private NettyClient nettyClient;


  private List<HttpResposeFilter> httpResposeFilterList;

  public HttpClientOutboundHandler(String host,int port) {
    this.nettyClient = new NettyClient(host,port);
    httpResposeFilterList = new ArrayList<>();
  }

  void addHttpReponseFilter(HttpResposeFilter httpResposeFilter) {
    httpResposeFilterList.add(httpResposeFilter);
  }

  public void flush(final FullHttpRequest fullHttpRequest, final ChannelHandlerContext ctx,
      String url) {
    System.out.println("the url:" + url);
    if (fullHttpRequest.method() == HttpMethod.GET) {
      nettyClient.getChannel().writeAndFlush(url);
    } else if (fullHttpRequest.method() == HttpMethod.POST) {

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
        for (HttpResposeFilter fullHttpResponse: httpResposeFilterList) {
          fullHttpResponse.filter(response);
        }
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
