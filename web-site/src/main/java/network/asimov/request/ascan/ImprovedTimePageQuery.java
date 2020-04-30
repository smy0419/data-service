package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhangjing
 * @date 2020/4/17
 */
@Data
public class ImprovedTimePageQuery {
    @NotNull(message = "page.limit not null")
    @Min(value = 1, message = "page.limit greater than or equal to 1")
    @Max(value = 100, message = "page.limit less than or equal to 100")
    @ApiModelProperty(value = "Show Count Per-page，No more than 100", example = "10", required = true)
    private Integer limit;

    @ApiModelProperty(value = "The time stamp of the day 0 seconds，default 0", example = "1572192000000")
    private long time;

    @ApiModelProperty(value = "Whether to get the next page or previous page, default next", example = "1572192000000")
    private boolean next;

    @ApiModelProperty(value = "Data offset", example = "1572192000000")
    private long offset;
}
