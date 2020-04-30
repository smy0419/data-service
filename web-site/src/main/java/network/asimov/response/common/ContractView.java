package network.asimov.response.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-10-08
 */
@Data
@Builder
public class ContractView {
    @ApiModelProperty(value = "Contract ABI")
    private String abi;

    @ApiModelProperty(value = "Contract Address")
    private String address;
}
