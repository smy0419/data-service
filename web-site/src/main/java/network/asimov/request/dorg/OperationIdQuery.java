package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2020-01-19
 */
@Data
public class OperationIdQuery {
    @ApiModelProperty(value = "Operation ID", required = true)
    @NotBlank(message = "operation id not blank")
    @JsonProperty("operation_id")
    private String operationId;

    @ApiModelProperty(value = "Contract Address", example = "0x639e13bfd6ee527ebf78fb2f5d8668e4996f6e895b", required = true)
    @NotBlank(message = "contract_address not blank")
    @JsonProperty("contract_address")
    private String contractAddress;

    @ApiModelProperty(value = "Operation Type", example = "1", required = true)
    @NotBlank(message = "operation_type not null")
    @JsonProperty("operation_type")
    private Integer operationType;
}
