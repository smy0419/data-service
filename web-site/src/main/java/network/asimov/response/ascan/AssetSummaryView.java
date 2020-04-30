package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

/**
 * @author sunmengyuan
 * @date 2019-11-07
 */
@Data
@Builder
public class AssetSummaryView {
    @ApiModelProperty(value = "Asset")
    private AssetView asset;

    @ApiModelProperty(value = "Issue Time")
    private Long time;

    @JsonProperty(value = "transaction_volume")
    @ApiModelProperty(value = "24-hour Circulation")
    private String transactionVolume;

    @JsonProperty(value = "amount")
    @ApiModelProperty(value = "Issue Total Amount")
    private String amount;

//    @ApiModelProperty(value = "当前价格")
//    private Double price;
//
//    @JsonProperty(value = "market_cap")
//    @ApiModelProperty(value = "市场容量")
//    private String marketCap;
}
