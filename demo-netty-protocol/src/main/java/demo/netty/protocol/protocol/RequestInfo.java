package demo.netty.protocol.protocol;

import lombok.Data;

import java.util.Map;

@Data
public class RequestInfo {
    /**
     * 请求接口
     */
    private String api;
    /**
     * 接口参数
     */
    private Map<String,Object> params;
}
