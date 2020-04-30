package network.asimov.request.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-10-08
 */
@Data
public class VoteRequest {
    @NotNull(message = "proposal id not null")
    @ApiModelProperty(value = "Proposal ID", example = "1", required = true)
    @JsonProperty("proposal_id")
    private Long proposalId;

    @NotNull(message = "decision not null")
    @ApiModelProperty(value = "Vote Decision", example = "true：同意；false：不同意", required = true)
    private Boolean decision;

    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;
}
