package demo.netty.protocol.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ResponseInfoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof ResponseInfo)){
            throw new RuntimeException("request cast exception");
        }
        System.out.println(msg);
    }
}
