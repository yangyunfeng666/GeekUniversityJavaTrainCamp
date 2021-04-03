package netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import netty.client.ClientChannelHandlerInitalizer;

public class NettyClient {

  private Channel channel;

  public Channel getChannel() {
    if (this.channel == null) {
      return null;
    }
    return channel;
  }

  public NettyClient(String host, int port) {
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(eventLoopGroup)
        .channel(NioSocketChannel.class)
        .remoteAddress(new InetSocketAddress(host, port))
        .handler(new ClientChannelHandlerInitalizer());
    try {
      ChannelFuture channelFuture = bootstrap.connect().sync();
      this.channel = channelFuture.channel();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      eventLoopGroup.shutdownGracefully();
    }
  }
}
