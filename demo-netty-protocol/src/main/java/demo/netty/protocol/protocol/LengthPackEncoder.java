package demo.netty.protocol.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LengthPackEncoder extends MessageToByteEncoder<byte[]> {
    protected void encode(ChannelHandlerContext ctx, byte[] msg, ByteBuf out) throws Exception {
        int length = msg.length + 4;
        out.writeInt(length);
        out.writeBytes(msg);
        System.out.println("pack encoder:" + length);
    }
}
