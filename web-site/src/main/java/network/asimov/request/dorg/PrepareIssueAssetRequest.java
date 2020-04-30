package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Data
public class PrepareIssueAssetRequest {
    @ApiModelProperty(value = "Contract Address", required = true)
    @JsonProperty("contract_address")
    @AddressValidate
    private String contractAddress;

    @ApiModelProperty(value = "Asset Name", required = true)
    @JsonProperty("asset_name")
    @NotBlank(message = "asset_name not blank")
    private String assetName;

    @ApiModelProperty(value = "Asset Symbol", required = true)
    @JsonProperty("asset_symbol")
    @NotBlank(message = "asset_symbol not blank")
    private String assetSymbol;

    @ApiModelProperty(value = "Amount", required = true)
    @NotNull(message = "amount not null")
    private BigInteger amount;

    @ApiModelProperty(value = "Asset Type: 0-indivisible, 1-divisible", required = true)
    @NotNull(message = "asset_type not null")
    @JsonProperty("asset_type")
    private Integer assetType;

}
