package demo.netty.protocol.client;

import demo.netty.protocol.protocol.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.Map;

public class ProtocolClient {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthPackDecoder());
                    ch.pipeline().addLast(new LengthPackEncoder());
                    ch.pipeline().addLast(new ResponeInfoDecoder());
                    ch.pipeline().addLast(new RequestInfoEncoder());
                    ch.pipeline().addLast(new ResponseInfoHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setApi("HELLO_API");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("hello", "API");
            requestInfo.setParams(params);
            f.channel().writeAndFlush(requestInfo);

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
