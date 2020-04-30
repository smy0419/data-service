package network.asimov.chainrpc.service.contract;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import network.asimov.chainrpc.request.ChainRequest;
import network.asimov.chainrpc.service.ChainRpcService;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.service.ascan.BlockService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2019-09-26
 */
@Slf4j
@Service("systemContractService")
public class SystemContractService {
    @Resource(name = "chainRpcService")
    private ChainRpcService chainRpcService;

    @Resource(name = "blockService")
    private BlockService blockService;

    private final static String RPC_GET_GENESIS_CONTRACT_BY_HEIGHT = "getGenesisContractByHeight";

    public String getAbi(String address) {
        // Get the latest block currently synchronized to
        Optional<Long> currentHeight = blockService.getHandledBlockHeight();

        // Request chain
        List<Object> params = Lists.newArrayList(currentHeight.isPresent() ? currentHeight.get() : 0, address);
        ChainRequest chainRequest = ChainRequest.builder().method(RPC_GET_GENESIS_CONTRACT_BY_HEIGHT).params(params).build();

        JSONObject jsonObject = (JSONObject) chainRpcService.post(chainRequest);
        Boolean exist = jsonObject.getBoolean("exist");
        if (exist == null || !exist) {
            log.error("system abi not exist, contract address = {}", address);
            throw BusinessException.builder().message("system abi not exist").errorCode(ErrorCode.DATA_ERROR).build();
        }
        String abi = jsonObject.getString("abi");
        if (StringUtils.isBlank(abi)) {
            log.error("system abi not exist, contract address = {}", address);
            throw BusinessException.builder().message("system abi not exist").errorCode(ErrorCode.DATA_ERROR).build();
        }

        return abi;
    }
}
