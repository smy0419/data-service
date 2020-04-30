package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-12-18
 */
@Data
public class StartVoteRequest {
    @ApiModelProperty(value = "Contract Address", required = true)
    @JsonProperty("contract_address")
    @AddressValidate
    private String contractAddress;

    @ApiModelProperty(value = "Asset Name", required = true)
    @NotEmpty(message = "asset_name not blank")
    @JsonProperty("asset_name")
    private String assetName;

    @ApiModelProperty(value = "Asset Symbol", required = true)
    @NotEmpty(message = "asset_symbol not blank")
    @JsonProperty("asset_symbol")
    private String assetSymbol;

    @ApiModelProperty(value = "Asset Logo Url")
    private String logo;

    @ApiModelProperty(value = "Asset Type: 0-indivisible, 1-divisible", required = true)
    @NotNull(message = "asset_type not null")
    @JsonProperty("asset_type")
    private Integer assetType;

    @NotNull(message = "amount_or_voucher_id not blank")
    @ApiModelProperty(value = "Amount or Voucher ID", example = "1000000", required = true)
    @JsonProperty("amount_or_voucher_id")
    private Long amountOrVoucherId;

    @ApiModelProperty(value = "Asset Description）")
    @JsonProperty("asset_desc")
    private String assetDesc;

    @ApiModelProperty(value = "Transaction Data", required = true)
    @NotEmpty(message = "call_data not blank")
    @JsonProperty("call_data")
    private String callData;
}
