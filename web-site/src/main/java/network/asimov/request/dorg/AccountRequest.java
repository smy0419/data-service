package network.asimov.request.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-11-08
 */
@Data
public class AccountRequest {
    @ApiModelProperty(value = "Nickname", example = "JackMa", required = true)
    private String name;

    @ApiModelProperty(value = "Avatar", example = "http://sdsdds.png", required = true)
    private String icon;
}
