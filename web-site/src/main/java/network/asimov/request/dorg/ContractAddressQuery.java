package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2019-12-10
 */
@Data
public class ContractAddressQuery {
    @ApiModelProperty(value = "Contract Address", required = true)
    @NotBlank(message = "contract_address not blank")
    @AddressValidate
    @JsonProperty("contract_address")
    private String contractAddress;
}
