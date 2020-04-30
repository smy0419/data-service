package network.asimov.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-10-08
 */
@Data
@Builder
public class VoterView {
    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Name")
    private String name;

    @ApiModelProperty(value = "Vote Time")
    @JsonProperty(value = "vote_time")
    private long voteTime;
}
