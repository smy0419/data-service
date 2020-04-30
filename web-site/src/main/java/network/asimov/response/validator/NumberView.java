package network.asimov.response.validator;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Data
@Builder
public class NumberView {
    @ApiModelProperty(value = "Number")
    private long number;
}
