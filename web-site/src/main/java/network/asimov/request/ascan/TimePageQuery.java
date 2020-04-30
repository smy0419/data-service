package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author zhangjing
 * @date 2019-10-28
 */
@Data
public class TimePageQuery {
    @Valid
    @ApiModelProperty(required = true)
    @NotNull(message = "page required")
    private PageQuery page;

    @ApiModelProperty(value = "The time stamp of the day 0 seconds，default 0", example = "1572192000")
    private int time;
}
