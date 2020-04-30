package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2019-12-18
 */
@Data
public class IssueAssetCheckRequest {
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

    @ApiModelProperty(value = "Asset Logo")
    @JsonProperty("asset_logo")
    private String assetLogo;

    @ApiModelProperty(value = "Asset Number(indivisible)")
    @JsonProperty("asset_number")
    private String assetNumber;

    @ApiModelProperty(value = "Asset Description(indivisible)")
    @JsonProperty("asset_description")
    private String assetDescription;
}
