package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-31
 */

@Builder
@Data
public class AssetIssueView {
    @ApiModelProperty(value = "Asset Issue Time")
    private long time;

    @ApiModelProperty(value = "Issue Value")
    private String value;

    @ApiModelProperty(value = "Description of Number")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
}
