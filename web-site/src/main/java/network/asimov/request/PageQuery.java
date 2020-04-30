package network.asimov.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhangjing
 * @date 2019-03-05
 */
@Data
public class PageQuery {
    @NotNull(message = "page.index not null")
    @Min(value = 1, message = "page.index greater than or equal to 1")
    @ApiModelProperty(value = "Current Index, stat from 1", example = "1", required = true)
    private Integer index;

    @NotNull(message = "page.limit not null")
    @Min(value = 1, message = "page.limit greater than or equal to 1")
    @Max(value = 100, message = "page.limit less than or equal to 100")
    @ApiModelProperty(value = "Show Count Per-page，No more than 100", example = "10", required = true)
    private Integer limit;
}
