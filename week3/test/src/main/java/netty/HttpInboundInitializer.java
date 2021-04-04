package netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpInboundInitializer extends ChannelInitializer {


  @Override
  protected void initChannel(Channel channel) throws Exception {
    ChannelPipeline channelPipeline = channel.pipeline();
    channelPipeline.addLast(new HttpServerCodec());
    channelPipeline.addLast(new HttpObjectAggregator(1024 * 1024));
    channelPipeline.addLast(new HttpInboundHandler());
  }


}
