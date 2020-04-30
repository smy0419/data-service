package network.asimov.request.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2020-01-09
 */
@Data
public class TopQuery {
    @ApiModelProperty(value = "Query count", example = "3", required = true)
    @NotNull(message = "top required")
    private Integer top;
}
