package network.asimov.response.foundation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-11-12
 */
@Data
@Builder
public class MemberBooleanView {
    @ApiModelProperty(value = "Is Member: true-yes, false-no")
    private boolean member;

    @ApiModelProperty(value = "Name")
    private String name;
}
