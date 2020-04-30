package network.asimov.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-09-24
 */
@Data
@Builder
public class TxView {
    @ApiModelProperty(value = "Transaction Hash")
    @JsonProperty(value = "tx_hash")
    private String txHash;
}
