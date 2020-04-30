package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.PageView;
import network.asimov.response.common.AssetView;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-30
 */
@Builder
@Data
public class BlockDetailView {
    @ApiModelProperty(value = "Block Hash")
    private String hash;

    @ApiModelProperty(value = "Block Confirmations")
    private long confirmations;

    @ApiModelProperty(value = "Block Size")
    private int size;

    @ApiModelProperty(value = "Block Height")
    private long height;

    @ApiModelProperty(value = "Block Time")
    private long time;

    @ApiModelProperty(value = "Transaction Count")
    @JsonProperty(value = "tx_count")
    private long txCount;

    @ApiModelProperty(value = "Produced Address")
    private String produced;

    @ApiModelProperty(value = "Mining Reward")
    private AssetView reward;

    @ApiModelProperty(value = "Fee Bonus")
    private List<AssetView> fee;

    @ApiModelProperty(value = "Produce Round")
    private int round;

    @ApiModelProperty(value = "Produce Slot")
    private int slot;

    @ApiModelProperty(value = "Transaction List")
    @JsonProperty(value = "tx")
    private PageView<TransactionView> tx;
}
