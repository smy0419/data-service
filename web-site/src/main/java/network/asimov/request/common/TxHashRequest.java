package network.asimov.request.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2019-10-29
 */
@Data
public class TxHashRequest {
    @NotBlank(message = "tx hash not blank")
    @ApiModelProperty(value = "Transaction Hash", example = "96c4cb212254c59d15ae5b333cdbd515324090e67dd468c75dd8ab9cf9188480", required = true)
    @JsonProperty("tx_hash")
    private String txHash;
}
