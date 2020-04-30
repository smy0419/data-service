package network.asimov.response.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2020-01-10
 */
@Data
@Builder
public class PreparedTx {
    @ApiModelProperty(value = "Transaction Type: create-create new contract, call-call contract，empty-transfer，done-has been performed")
    private String type;

    @ApiModelProperty(value = "Receiver Address")
    private String to;

    @ApiModelProperty(value = "Transaction Data")
    private String data;
}
