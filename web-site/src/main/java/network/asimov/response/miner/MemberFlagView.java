package network.asimov.response.miner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-10-21
 */
@Data
@Builder
public class MemberFlagView {
    @ApiModelProperty(value = "Is Mining Member: true-yes, false-no")
    private Boolean member;
}
