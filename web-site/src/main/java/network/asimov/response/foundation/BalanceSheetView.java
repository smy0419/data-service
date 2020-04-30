package network.asimov.response.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-09-21
 */
@Data
@Builder
public class BalanceSheetView {
    @ApiModelProperty(value = "Asset ID")
    private String asset;

    @ApiModelProperty(value = "Amount")
    private String amount;

    @ApiModelProperty(value = "Proposal Type")
    @JsonProperty(value = "proposal_type")
    private int proposalType;

    @ApiModelProperty(value = "Transfer Type")
    @JsonProperty(value = "transfer_type")
    private int transferType;

    @ApiModelProperty(value = "Time")
    private long time;

    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Asset Symbol")
    private String symbol;

    @ApiModelProperty(value = "Transaction Hash")
    @JsonProperty(value = "tx_hash")
    private String txHash;
}
