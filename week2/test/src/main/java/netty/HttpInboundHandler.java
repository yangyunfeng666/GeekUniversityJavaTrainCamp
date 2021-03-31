package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

  private HttpOutboundHandler httpOutboundHander;

  public HttpInboundHandler() {
    this.httpOutboundHander = new HttpOutboundHandler();
  }

  private String serverUrl = "http://localhost:8080";

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//    super.channelRead(ctx, msg);
    FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
    String url = fullHttpRequest.uri();
    if (url.contains("/test")) {//路由
      //输出数据到浏览器
      httpOutboundHander.flush(fullHttpRequest,ctx,serverUrl);
    }
    ReferenceCountUtil.release(msg);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//    super.channelReadComplete(ctx);
    ctx.flush();
  }


}
