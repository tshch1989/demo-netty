package demo.netty.protocol.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RequestInfoDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteBufInputStream bis = new ByteBufInputStream(in);
        RequestInfo requestInfo = JSONObject.parseObject(bis, RequestInfo.class);
        out.add(requestInfo);
    }
}
