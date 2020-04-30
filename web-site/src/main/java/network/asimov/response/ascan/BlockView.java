package network.asimov.response.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-28
 */

@Builder
@Data
public class BlockView {
    @ApiModelProperty(value = "Block Height")
    private long height;

    @ApiModelProperty(value = "Transaction Count")
    private long transactions;

    @ApiModelProperty(value = "Block Time")
    private long time;

    @ApiModelProperty(value = "Produced Address")
    private String produced;

    @ApiModelProperty(value = "Mining Reward")
    private AssetView reward;

    @ApiModelProperty(value = "Fee Bonus")
    private List<AssetView> fee;
}
