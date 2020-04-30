package network.asimov.chainrpc.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-09-21
 */

@Data
public class ChainRequest {
    private static final String RPC_PREFIX = "asimov_";
    private Long id;
    @JSONField(defaultValue = "jsonrpc")
    private String jsonRpc;
    private String method;
    private List<Object> params;

    @Builder
    public ChainRequest(String method, List<Object> params) {
        this.id = System.currentTimeMillis();
        this.jsonRpc = "2.0";
        this.method = String.format("%s%s", RPC_PREFIX, method);
        this.params = params;
    }
}
