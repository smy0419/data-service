package network.asimov.request.miner;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author sunmengyuan
 * @date 2019-10-09
 */
@Data
public class ProposalRequest {
    @NotBlank(message = "asset not blank")
    @ApiModelProperty(value = "Asset", example = "0x000000000000000000000000", required = true)
    @JsonProperty("asset")
    private String asset;

    @NotBlank(message = "comment not blank")
    @Size(max = 100, message = "comment max size 100")
    @ApiModelProperty(value = "Proposal Comment", example = "This is proposal comment.", required = true)
    @JsonProperty("comment")
    private String comment;

    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;
}
