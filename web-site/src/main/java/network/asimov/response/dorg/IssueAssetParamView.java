package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Data
@Builder
public class IssueAssetParamView {
    @ApiModelProperty(value = "Subject")
    private String subject;

    @ApiModelProperty(value = "Vote Type")
    @JsonProperty("vote_type")
    private Integer voteType;

    @ApiModelProperty(value = "Total Participants")
    @JsonProperty("total_participants")
    private Long totalParticipants;

    @ApiModelProperty(value = "Percent")
    private Integer percent;

    @ApiModelProperty(value = "End Time")
    @JsonProperty("end_time")
    private Long endTime;

    @ApiModelProperty(value = "Function")
    private String func;

    @ApiModelProperty(value = "Parameter")
    private String param;
}
