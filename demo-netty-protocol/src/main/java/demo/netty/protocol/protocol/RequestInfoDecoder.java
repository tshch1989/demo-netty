package demo.netty.protocol.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class RequestInfoDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            ByteBufInputStream bis = new ByteBufInputStream(in);
            RequestInfo requestInfo = JSONObject.parseObject(bis, RequestInfo.class);
            out.add(requestInfo);
        }finally {
            ReferenceCountUtil.release(in);
        }
    }
}
