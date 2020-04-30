package network.asimov.response.foundation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.PersonView;

/**
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Data
@Builder
public class ProposalView {
    @ApiModelProperty(value = "Proposal ID")
    @JsonProperty("proposal_id")
    private long proposalId;

    @ApiModelProperty(value = "Proposal Type: 0-elect 1-impeach， 2-invest")
    @JsonProperty("type")
    private int type;

    @ApiModelProperty(value = "Proposer")
    @JsonProperty("proposer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonView proposer;

    @ApiModelProperty(value = "Proposal Comment")
    @JsonProperty("comment")
    private String comment;

    @ApiModelProperty(value = "Proposal Time")
    private long time;

    @ApiModelProperty(value = "Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed")
    @JsonProperty("tx_status")
    private int txStatus;

    @ApiModelProperty(value = "Proposal Status: 0-ongoing 1、effective 2、expired")
    @JsonProperty("proposal_status")
    private int proposalStatus;
}
