package network.asimov.chainrpc.service.contract;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.chainrpc.pojo.ActionDTO;
import network.asimov.chainrpc.pojo.MinerBlockDTO;
import network.asimov.chainrpc.request.ChainRequest;
import network.asimov.chainrpc.service.ChainRpcService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhangjing
 * @date 2019-09-26
 */
@Service("validatorCommitteeService")
@Slf4j
public class ValidatorCommitteeService implements CommandLineRunner {
    @Resource(name = "chainRpcService")
    private ChainRpcService chainRpcService;

    @Resource(name = "systemContractService")
    private SystemContractService systemContractService;

    private ActionDTO minerAction;

    /**
     * Implements CommandLineRunner, executed at project start up.
     *
     * @param args
     */
    @Override
    public void run(String... args) {
        try {
            this.cacheAction();
        } catch (Exception e) {
            log.error("get miner action failed.", e);
            System.exit(-1);
        }
    }

    /**
     * 获取MinerAction
     *
     * @return MinerAction
     */
    public ActionDTO getMinerAction() {
        return minerAction;
    }

    /**
     * Get number of miner's mining block
     *
     * @param address address of miner
     * @return MinerStakeBlockDTO
     */
    public MinerBlockDTO getMinerBlocks(String address) {
        String abi = systemContractService.getAbi(ChainConstant.CONSENSUS_MANAGEMENT_ADDRESS);

        String funcName = "getValidatorBlockInfo";
        Function function = new Function(
                funcName,
                Arrays.asList(new Address(ChainConstant.ADDRESS_BIT_SIZE, address.substring(2))),
                Collections.emptyList());
        String functionHex = FunctionEncoder.encode(function);

        // Request chain
        List<Object> params = Lists.newArrayList(address, ChainConstant.CONSENSUS_MANAGEMENT_ADDRESS, functionHex.substring(2), funcName, abi);
        ChainRequest chainRequest = ChainRequest.builder().method(ChainConstant.RPC_CALL_READ_ONLY_FUNCTION).params(params).build();
        JSONArray jsonArray = (JSONArray) chainRpcService.post(chainRequest);
        // Parsing results
        Long planedBlocks = jsonArray.getLong(0);
        Long actualBlocks = jsonArray.getLong(1);

        return MinerBlockDTO.builder()
                .plannedBlocks(planedBlocks)
                .actualBlocks(actualBlocks)
                .build();
    }

    /**
     * Cache MinerActionDTO
     */
    private void cacheAction() {
        List<Integer> proposalList = Lists.newArrayList(0);
        List<Integer> voteList = Lists.newArrayList(0);
        minerAction = ActionDTO.builder().proposal(proposalList).vote(voteList).build();
    }
}
