package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

/**
 * @author sunmengyuan
 * @date 2019-11-06
 */
@Data
@Builder
public class AssetDetailView {
    @ApiModelProperty(value = "Asset")
    private AssetView asset;

    @JsonProperty(value = "transaction_count")
    @ApiModelProperty(value = "Transaction Count")
    private Long transactionCount;

    @ApiModelProperty(value = "Current Price")
    private Double price;

    @JsonProperty(value = "market_cap")
    @ApiModelProperty(value = "Market Capacity")
    private String marketCap;

    @JsonProperty(value = "transaction_volume")
    @ApiModelProperty(value = "24-hour Circulation")
    private String transactionVolume;

    @JsonProperty(value = "holder_counts")
    @ApiModelProperty(value = "Asset Holders")
    private Long holderCounts;

    @JsonProperty(value = "issue_address")
    @ApiModelProperty(value = "Asset Issue Address")
    private String issueAddress;

    @JsonProperty(value = "issue_time")
    @ApiModelProperty(value = "Asset Issue Time")
    private Long issueTime;

    @JsonProperty(value = "amount")
    @ApiModelProperty(value = "Asset Total Amount")
    private String amount;
}
