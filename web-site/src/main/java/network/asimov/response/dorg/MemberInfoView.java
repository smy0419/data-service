package network.asimov.response.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-12-16
 */
@Data
@Builder
public class MemberInfoView {
    @ApiModelProperty(value = "Member Address")
    private String address;

    @ApiModelProperty(value = "Role Type: 0-president，1-member")
    private Integer roleType;

    @ApiModelProperty(value = "Name")
    private String name;

    @ApiModelProperty(value = "Avatar")
    private String avatar;

}
