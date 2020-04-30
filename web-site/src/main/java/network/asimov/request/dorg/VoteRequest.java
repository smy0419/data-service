package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2020-01-08
 */
@Data
public class VoteRequest {
    @NotNull(message = "contract_address not null")
    @ApiModelProperty(value = "Contract Address", example = "0x63fbab8d621166db1708d002f9c31287424b9ba37d", required = true)
    @JsonProperty("contract_address")
    private String contractAddress;

    @NotNull(message = "proposal id not null")
    @ApiModelProperty(value = "Proposal ID", example = "1", required = true)
    @JsonProperty("proposal_id")
    private Long proposalId;

    @NotNull(message = "decision not null")
    @ApiModelProperty(value = "Decision", example = "true：同意；false：不同意", required = true)
    private Boolean decision;

    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;
}
