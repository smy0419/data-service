package network.asimov.response.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-09-27
 */
@Data
@Builder
public class ActionView {
    @ApiModelProperty(value = "Proposal Action: 0-elect 1-impeach， 2-invest")
    @JsonProperty(value = "proposal_action")
    @Builder.Default
    private List<Integer> proposalAction = Lists.newArrayList();

    @ApiModelProperty(value = "Vote Action: 0-elect 1-impeach， 2-invest")
    @JsonProperty(value = "vote_action")
    @Builder.Default
    private List<Integer> voteAction = Lists.newArrayList();

    @ApiModelProperty(value = "Todo Count")
    @JsonProperty(value = "todo_count")
    @Builder.Default
    private Map<String, Long> todoCount = Maps.newHashMap();
}
