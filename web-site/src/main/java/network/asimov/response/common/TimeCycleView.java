package network.asimov.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-10-15
 */

@Data
@Builder
public class TimeCycleView {
    @ApiModelProperty(value = "Start Time")
    @JsonProperty("start_time")
    private long startTime;

    @ApiModelProperty(value = "End Time")
    @JsonProperty("end_time")
    private long endTime;
}
