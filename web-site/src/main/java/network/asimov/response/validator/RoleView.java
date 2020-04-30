package network.asimov.response.validator;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-11-15
 */
@Data
@Builder
public class RoleView {
    @ApiModelProperty(value = "User Role: 0-nothing, 1-validator")
    private Integer role;
}
