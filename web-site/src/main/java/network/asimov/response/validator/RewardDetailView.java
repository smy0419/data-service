package network.asimov.response.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-11-18
 */
@Data
@Builder
public class RewardDetailView {
    @ApiModelProperty(value = "Block Height")
    private Long height;

    @ApiModelProperty(value = "Time")
    private Long time;

    @ApiModelProperty(value = "Transaction Hash")
    @JsonProperty(value = "tx_hash")
    private String txHash;

    @ApiModelProperty(value = "Earning List")
    private List<EarningAssetView> earning;
}
