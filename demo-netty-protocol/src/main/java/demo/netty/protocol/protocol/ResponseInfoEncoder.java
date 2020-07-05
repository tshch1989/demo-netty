package demo.netty.protocol.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ResponseInfoEncoder extends MessageToMessageEncoder<ResponseInfo> {
    protected void encode(ChannelHandlerContext ctx, ResponseInfo msg, List<Object> out) throws Exception {
        byte[] msgBytes = JSONObject.toJSONBytes(msg);
        out.add(msgBytes);
    }
}
