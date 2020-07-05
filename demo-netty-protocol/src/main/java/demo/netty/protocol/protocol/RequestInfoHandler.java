package demo.netty.protocol.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

public class RequestInfoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof RequestInfo)){
            throw new RuntimeException("request cast exception");
        }
        RequestInfo requestInfo = (RequestInfo) msg;
        String api = requestInfo.getApi();
        Map<String, Object> params = requestInfo.getParams();
        if("HELLO_API".equals(api)){
            String hello = (String)params.get("hello");
            ResponseInfo responseInfo = new ResponseInfo();
            responseInfo.setBody("Hello " + hello + ".");
            responseInfo.setCode("200");
            responseInfo.setMsg("success");
            ctx.writeAndFlush(responseInfo);
        }else {
            ResponseInfo responseInfo = new ResponseInfo("400", "fail", "");
            ctx.writeAndFlush(responseInfo);
        }
    }
}
