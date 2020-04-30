package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Data
public class ExpenseRequest {
    @ApiModelProperty(value = "Contract Address", example = "0x......", required = true)
    @NotBlank(message = "contract_address not blank")
    @JsonProperty("contract_address")
    private String contractAddress;

    @ApiModelProperty(value = "Target Address", example = "0x......", required = true)
    @NotBlank(message = "target_address not blank")
    @JsonProperty("target_address")
    private String targetAddress;

    @NotBlank(message = "asset not blank")
    @ApiModelProperty(value = "Asset", example = "000000000000000000000000", required = true)
    private String asset;

    @NotNull(message = "asset_type not null")
    @ApiModelProperty(value = "Asset Type: 0-indivisible, 1-divisible", required = true)
    @JsonProperty("asset_type")
    private Integer assetType;

    @NotNull(message = "amount not blank")
    @ApiModelProperty(value = "Amount", example = "1000000", required = true)
    private Long amount;

    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;
}
