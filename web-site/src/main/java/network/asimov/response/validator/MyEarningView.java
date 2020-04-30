package network.asimov.response.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-11-15
 */
@Data
@Builder
public class MyEarningView {
    @ApiModelProperty(value = "My Total Earning")
    @JsonProperty(value = "total_earning")
    private List<EarningAssetView> totalEarning;

    @ApiModelProperty(value = "BTC Miner")
    @JsonProperty(value = "btc_miner")
    private List<BtcMinerView> btcMiner;

    @ApiModelProperty(value = "Validated Block Count")
    @JsonProperty(value = "validated_blocks")
    private Long validatedBlocks;
}