package network.asimov.response.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-11-08
 */
@Data
@Builder
public class AccountView {
    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Nick Name")
    private String name;

    @ApiModelProperty(value = "Avatar")
    private String icon;
}
