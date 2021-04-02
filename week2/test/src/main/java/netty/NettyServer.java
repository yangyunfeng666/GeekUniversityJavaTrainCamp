package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {


  public static void main(String[] args) {
    EventLoopGroup bootEventGroup = new NioEventLoopGroup(1);
    EventLoopGroup workEventGroup = new NioEventLoopGroup(2);
    ServerBootstrap server = new ServerBootstrap();
    server
        .option(ChannelOption.SO_BACKLOG, 128) //建立连接前的最大连接数
        .childOption(ChannelOption.TCP_NODELAY, true)//小包密集发送
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.SO_REUSEADDR, true)
        .childOption(ChannelOption.SO_RCVBUF, 32 * 1024) //缓冲区
        .childOption(ChannelOption.SO_SNDBUF, 32 * 1024)
        .childOption(ChannelOption.SO_TIMEOUT, 60) //在TIME_WAIT 连接复用
        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    try {
      server.group(bootEventGroup, workEventGroup).channel(
          NioServerSocketChannel.class).
          handler(new LoggingHandler(LogLevel.DEBUG))
          .childHandler(new HttpInboundInitializer());
      Channel channel = server.bind(8081).sync().channel();
      System.out.println("the server on port：" + 8081 + " start ");
      channel.closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      bootEventGroup.shutdownGracefully();
      workEventGroup.shutdownGracefully();
    }
  }

}
