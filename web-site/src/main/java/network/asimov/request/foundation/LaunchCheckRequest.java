package network.asimov.request.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author zhangjing
 * @date 2019-10-17
 */
@Data
public class LaunchCheckRequest {
    @NotNull(message = "proposal_type not blank")
    @ApiModelProperty(value = "Proposal Type: 0-elect 1-impeach 2-invest", example = "1", required = true)
    @JsonProperty("proposal_type")
    private Integer proposalType;

    @ApiModelProperty(value = "Target Address", example = "0x664d34218237d4958a7bf6b60151f2092f50fa81ee")
    @JsonProperty("target_address")
    @Size(min = 44, max = 44, message = "Address length must be 44 bits")
    @AddressValidate
    private String targetAddress;

    @ApiModelProperty(value = "Invest Amount", example = "1000000000")
    @JsonProperty("invest_amount")
    private Long investAmount;

}
