package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author sunmengyuan
 * @date 2019-12-12
 */
@Data
public class ModifyOrgNameRequest {
    @ApiModelProperty(value = "Organization Name")
    @NotBlank(message = "org_name name not blank")
    @Size(max = 20, message = "org_name name max size 20")
    @JsonProperty("org_name")
    private String orgName;

    @NotBlank(message = "contract_address not blank")
    @ApiModelProperty(value = "Contract Address", required = true)
    @AddressValidate
    @JsonProperty("contract_address")
    private String contractAddress;

    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;
}
