package network.asimov.chainrpc.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import network.asimov.chainrpc.pojo.ContractSourceDTO;
import network.asimov.chainrpc.request.ChainRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2019-11-07
 */
@Service("contractService")
public class ContractService {
    @Resource(name = "chainRpcService")
    private ChainRpcService chainRpcService;

    private final static String RPC_GET_CONTRACT_TEMPLATE = "getContractTemplateInfoByName";

    public Optional<ContractSourceDTO> getSource(int category, String templateName) {
        List<Object> params = Lists.newArrayList(category, templateName);
        ChainRequest chainRequest = ChainRequest.builder().method(RPC_GET_CONTRACT_TEMPLATE).params(params).build();
        try {
            JSONObject jsonObject = (JSONObject) chainRpcService.post(chainRequest);
            ContractSourceDTO contractSourceDto = ContractSourceDTO.builder()
                    .category(jsonObject.getInteger("category"))
                    .templateName(jsonObject.getString("template_name"))
                    .abi(jsonObject.getString("abi"))
                    .byteCode(jsonObject.getString("byte_code"))
                    .source(jsonObject.getString("source"))
                    .build();
            return Optional.of(contractSourceDto);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
