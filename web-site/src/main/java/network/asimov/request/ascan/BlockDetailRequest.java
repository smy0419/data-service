package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

/**
 * @author zhangjing
 * @date 2019-10-28
 */
@Data
public class BlockDetailRequest {
    @ApiModelProperty(value = "Block Height", example = "0")
    private Long height;

    @ApiModelProperty(value = "Block Hash", example = "8492587c11a3d379f1adf@ApiModel(description = \"The height and hash conditions must be chosen either way, with height being preferred if both are available\")\n068b103ba8e8313e2d7342f48ab3bc62c4d5a571475")
    private String hash;

    private PageQuery page;
}
