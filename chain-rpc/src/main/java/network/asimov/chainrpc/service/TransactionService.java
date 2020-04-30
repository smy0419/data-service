package network.asimov.chainrpc.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import network.asimov.chainrpc.request.ChainRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-08
 */

@Service("chainRpcTxService")
public class TransactionService {
    @Resource(name = "chainRpcService")
    private ChainRpcService chainRpcService;

    private final static String RPC_SEND_RAW_TRANSACTION = "sendRawTransaction";

    private final static String RPC_GET_MEM_POOL_TRANSACTIONS = "getMempoolTransactions";

    /**
     * Send transaction to chain
     *
     * @param callData Transaction Data
     * @return Transaction Hash
     */
    public String sendRawTransaction(String callData) {
        List<Object> params = Lists.newArrayList(callData);
        ChainRequest chainRequest = ChainRequest.builder().method(RPC_SEND_RAW_TRANSACTION).params(params).build();
        return (String) chainRpcService.post(chainRequest);
    }

    /**
     * Query data in the transaction member pool based on transaction hash.
     *
     * @param txHashList List Of Transaction Hash
     * @return List Of Transaction Hash
     */
    public List<String> getMemPoolTransactions(List<String> txHashList) {
        List<String> containTxHash = Lists.newArrayList();
        ChainRequest chainRequest = ChainRequest.builder().method(RPC_GET_MEM_POOL_TRANSACTIONS).params(Collections.singletonList(txHashList)).build();
        Object result = chainRpcService.post(chainRequest);
        if (result instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) result;
            for (String txHash : txHashList) {
                if (jsonObject.containsKey(txHash)) {
                    containTxHash.add(txHash);
                }
            }
        }

        return containTxHash;
    }
}
