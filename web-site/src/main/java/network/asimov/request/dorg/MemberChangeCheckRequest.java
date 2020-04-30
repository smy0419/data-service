package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author sunmengyuan
 * @date 2019-12-16
 */
@Data
public class MemberChangeCheckRequest {
    @ApiModelProperty(value = "Contract Address", required = true)
    @NotEmpty(message = "contract_address not blank")
    @JsonProperty("contract_address")
    private String contractAddress;

    @ApiModelProperty(value = "Change Type: 4-remove member, 7-invite president, 9-invite member", required = true)
    @NotNull(message = "change_type not null")
    @JsonProperty("change_type")
    private Integer changeType;

    @ApiModelProperty(value = "Target Address", required = true)
    @Size(min = 44, max = 44, message = "address length must be 44 bits")
    @AddressValidate
    @JsonProperty("target_address")
    private String targetAddress;
}
