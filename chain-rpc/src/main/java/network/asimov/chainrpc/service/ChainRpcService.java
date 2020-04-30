package network.asimov.chainrpc.service;

import network.asimov.chainrpc.request.ChainRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhangjing
 * @date 2019-09-21
 */

@Service("chainRpcService")
public class ChainRpcService {
    @Value("${block-chain-endpoint}")
    private String chainEndpoint;

    @Resource(name = "okHttpService")
    private OkHttpService okHttpService;

    private static final String RESULT_KEY = "result";

    public Object post(ChainRequest chainRequest) {
        return okHttpService.post(chainEndpoint, chainRequest, RESULT_KEY);
    }
}
