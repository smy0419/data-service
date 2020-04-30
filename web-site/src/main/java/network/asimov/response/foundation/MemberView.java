package network.asimov.response.foundation;

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
public class MemberView {
    @ApiModelProperty(value = "Member Information")
    @JsonProperty(value = "member")
    private PersonView member;

    @ApiModelProperty(value = "Proposal Count")
    @JsonProperty(value = "proposal_count")
    private long proposalCount;

    @ApiModelProperty(value = "Vote Count")
    @JsonProperty(value = "vote_count")
    private long voteCount;
}
