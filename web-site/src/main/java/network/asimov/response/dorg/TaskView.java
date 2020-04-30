package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.PersonView;

/**
 * @author sunmengyuan
 * @date 2019-12-12
 */
@Data
@Builder
public class TaskView {
    @ApiModelProperty(value = "Proposer")
    @JsonProperty("proposer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonView proposer;

    @ApiModelProperty(value = "Proposal ID")
    @JsonProperty("proposal_id")
    private Long proposalId;

    @ApiModelProperty(value = "Proposal Type: 0-create asset")
    private Integer proposalType;

    @ApiModelProperty(value = "Create Time")
    @JsonProperty("create_time")
    private Long createTime;
}