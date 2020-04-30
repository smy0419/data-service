package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-11-04
 */
@Builder
@Data
public class AddressTransactionView {
    @ApiModelProperty(value = "Transaction Hash")
    @JsonProperty(value = "tx_hash")
    private String txHash;

    @ApiModelProperty(value = "Transaction Time")
    private long time;

    @ApiModelProperty(value = "Transaction Fee")
    private List<AssetView> fee;
}
