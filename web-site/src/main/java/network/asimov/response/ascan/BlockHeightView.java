package network.asimov.response.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-10-28
 */
@Builder
@Data
public class BlockHeightView {
    @ApiModelProperty(value = "Block Height")
    private long height;
}
