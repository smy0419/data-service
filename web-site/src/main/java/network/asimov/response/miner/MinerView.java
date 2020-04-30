package network.asimov.response.miner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.PersonView;

/**
 * @author sunmengyuan
 * @date 2019-09-21
 */

@Data
@Builder
public class MinerView {
    @ApiModelProperty(value = "Member")
    private PersonView member;

    @ApiModelProperty(value = "Produce Efficiency")
    private int efficiency;

    @ApiModelProperty(value = "Produced")
    @JsonProperty(value = "produced")
    private long produced;

    @ApiModelProperty(value = "Proposal Count")
    @JsonProperty(value = "proposal_count")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long proposalCount;
}
