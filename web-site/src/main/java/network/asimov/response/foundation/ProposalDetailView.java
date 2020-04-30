package network.asimov.response.foundation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;
import network.asimov.response.common.PersonView;
import network.asimov.response.common.VoterView;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-10-17
 */
@Data
@Builder
public class ProposalDetailView {
    @ApiModelProperty(value = "Proposal ID")
    @JsonProperty("proposal_id")
    private long proposalId;

    @ApiModelProperty(value = "Proposal Type: 0-elect 1-impeach， 2-invest")
    private int type;

    @ApiModelProperty(value = "Proposer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonView proposer;

    @ApiModelProperty(value = "Proposal Comment")
    private String comment;

    @ApiModelProperty(value = "Proposal Time")
    private long time;

    @ApiModelProperty(value = "Proposal End Time")
    @JsonProperty("end_time")
    private long endTime;

    @ApiModelProperty(value = "Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed")
    @JsonProperty("tx_status")
    private int txStatus;

    @ApiModelProperty(value = "Proposal Status: 0-ongoing 1、effective 2、expired")
    @JsonProperty("proposal_status")
    private int proposalStatus;

    @ApiModelProperty(value = "Agree Voter List")
    @JsonProperty("agree_voters")
    private List<VoterView> agreeVoters;

    @ApiModelProperty(value = "Disagree Voters List")
    @JsonProperty("disagree_voters")
    private List<VoterView> disagreeVoters;

    @ApiModelProperty(value = "Not Yet Voter List")
    @JsonProperty("not_yet_voters")
    private List<VoterView> notYetVoters;

    @ApiModelProperty(value = "Pass Support Rate")
    @JsonProperty("pass_support_rate")
    private Double passSupportRate;

    @ApiModelProperty(value = "Target Person")
    @JsonProperty("target_person")
    private PersonView targetPerson;

    @ApiModelProperty(value = "Invest Asset")
    @JsonProperty("invest_asset")
    private AssetView investAsset;

    @ApiModelProperty(value = "Allow Vote: true-yes, false-no")
    @JsonProperty("allow_vote")
    private Boolean allowVote;
}
