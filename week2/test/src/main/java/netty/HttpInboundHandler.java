package netty;

import filter.HttpRequestFilter;
import filter.MyHttpRequestFilter;
import filter.MyHttpResponseFilter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayList;
import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

  private HttpOutboundHandler httpOutboundHander;

  private List<HttpRequestFilter> requestFilterList;

  public HttpInboundHandler() {
    this.requestFilterList = new ArrayList<>();
    this.requestFilterList.add(new MyHttpRequestFilter());
    this.httpOutboundHander = new HttpOutboundHandler();
    this.httpOutboundHander.addHttpReponseFilter(new MyHttpResponseFilter());
  }

  private String serverUrl = "http://localhost:8080";

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//    super.channelRead(ctx, msg);
    FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
    String url = fullHttpRequest.uri();
    requestFilterList.forEach(s -> {
      s.filter(fullHttpRequest, ctx);
    });
    if (url.contains("/test")) {//路由
      //输出数据到浏览器
      httpOutboundHander.flush(fullHttpRequest, ctx, serverUrl);
    }
    ReferenceCountUtil.release(msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//    super.channelReadComplete(ctx);
    ctx.flush();
  }
}
