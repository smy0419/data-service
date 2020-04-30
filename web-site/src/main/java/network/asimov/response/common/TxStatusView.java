package network.asimov.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-10-29
 */
@Data
@Builder
public class TxStatusView {
    @ApiModelProperty(value = "Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed")
    @JsonProperty(value = "tx_status")
    private int txStatus;
}
