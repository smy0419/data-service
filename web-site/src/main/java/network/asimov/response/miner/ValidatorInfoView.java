package network.asimov.response.miner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-09-30
 */

@Data
@Builder
public class ValidatorInfoView {
    @ApiModelProperty(value = "Produced Block Count")
    private Long produced;
}
