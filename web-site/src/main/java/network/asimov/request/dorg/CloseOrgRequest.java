package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2019-12-12
 */
@Data
public class CloseOrgRequest {
    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;

    @NotBlank(message = "contract_address not blank")
    @ApiModelProperty(value = "Contract Address", example = "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2", required = true)
    @JsonProperty("contract_address")
    private String contractAddress;
}
