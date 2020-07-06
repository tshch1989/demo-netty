package demo.netty.protocol.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * length:4byte
 * body:length-4byte
 */
public class LengthPackDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() < 4){
            return;
        }
        int length = in.getInt(in.readerIndex());
        if(in.readableBytes() < length){
            return;
        }
        try {
            ByteBuf retainedSlice = in.retainedSlice(in.readerIndex() + 4, length);
            in.readerIndex(in.readerIndex() + length);
            out.add(retainedSlice);
        }finally {
            ReferenceCountUtil.release(in);
        }
    }

}
