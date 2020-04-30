package network.asimov.response.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-12-17
 */
@Data
@Builder
public class ActivityView {
    @ApiModelProperty(value = "Activity ID")
    private String id;

    @ApiModelProperty(value = "Activity State: 0-processing, 1-successful, 2-failed")
    private Integer state;
}
