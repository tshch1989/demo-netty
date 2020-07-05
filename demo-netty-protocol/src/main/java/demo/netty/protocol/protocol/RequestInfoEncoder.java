package demo.netty.protocol.protocol;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class RequestInfoEncoder extends MessageToMessageEncoder<RequestInfo> {
    protected void encode(ChannelHandlerContext ctx, RequestInfo msg, List<Object> out) throws Exception {
        byte[] msgBytes = JSONObject.toJSONBytes(msg);
        out.add(msgBytes);
    }
}
