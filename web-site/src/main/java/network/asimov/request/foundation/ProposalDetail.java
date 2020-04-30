package network.asimov.request.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author sunmengyuan
 * @date 2019-10-22
 */
@Data
public class ProposalDetail {
    @NotNull(message = "proposal_type not blank")
    @ApiModelProperty(value = "Proposal Type: 0-elect 1-impeach 2-invest", example = "1", required = true)
    @JsonProperty("proposal_type")
    private Integer proposalType;

    @NotBlank(message = "comment not blank")
    @Size(max = 100, message = "comment max size 100")
    @ApiModelProperty(value = "Proposal Comment", example = "This is proposal comment.", required = true)
    private String comment;

    @ApiModelProperty(value = "Target Address", example = "0x660ed67abdd8ff954fdc94035d8fa339953cee7c15", required = true)
    @JsonProperty("target_address")
    @AddressValidate
    private String targetAddress;

    @ApiModelProperty(value = "Invest Asset", example = "000000000000000000000000(if proposal_type==2,required)")
    @JsonProperty("invest_asset")
    private String investAsset;

    @ApiModelProperty(value = "Invest Amount", example = "999999(if proposal_type==2,required)")
    @JsonProperty("invest_amount")
    private Long investAmount;


}
