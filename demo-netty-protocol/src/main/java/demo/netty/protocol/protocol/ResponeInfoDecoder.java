package demo.netty.protocol.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class ResponeInfoDecoder extends ByteToMessageDecoder {
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            ByteBufInputStream bis = new ByteBufInputStream(in);
            ResponseInfo responseInfo = JSONObject.parseObject(bis, ResponseInfo.class);
            out.add(responseInfo);
        }finally {
            ReferenceCountUtil.release(in);
        }
    }
}
