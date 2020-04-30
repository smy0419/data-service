package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2020-01-04
 */
@Data
public class ConfirmMemberChangeRequest {
    @ApiModelProperty(value = "Organization Contract Address", required = true)
    @NotEmpty(message = "contractAddress not blank")
    @JsonProperty("contract_address")
    private String contractAddress;

    @ApiModelProperty(value = "Change Type: 8-new president join, 10-new member join", required = true)
    @NotNull(message = "change_type not null")
    @JsonProperty("change_type")
    private Integer changeType;

    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @NotBlank(message = "call_data not blank")
    @JsonProperty("call_data")
    private String callData;
}
