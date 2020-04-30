package network.asimov.request.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.ProposalValidate;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @author zhangjing
 * @date 2019-10-17
 */
@Data
public class LaunchRequest {
    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;

    @ProposalValidate
    @Valid
    @JsonProperty("proposal_detail")
    private ProposalDetail proposalDetail;
}
