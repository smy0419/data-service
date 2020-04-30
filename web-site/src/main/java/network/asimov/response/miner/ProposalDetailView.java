package network.asimov.response.miner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.PersonView;
import network.asimov.response.common.VoterView;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-09-29
 */
@Data
@Builder
public class ProposalDetailView {
    @ApiModelProperty(value = "Proposal Id")
    @JsonProperty("proposal_id")
    private long proposalId;

    @ApiModelProperty(value = "Proposal Type: 0-confirm fee asset")
    @JsonProperty("type")
    private int type;

    @ApiModelProperty(value = "Asset Name")
    @JsonProperty("asset_name")
    private String assetName;

    @ApiModelProperty(value = "Proposer")
    @JsonProperty("proposer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonView proposer;

    @ApiModelProperty(value = "Proposal Comment")
    @JsonProperty("comment")
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

    @ApiModelProperty(value = "Disagree Voter List")
    @JsonProperty("disagree_voters")
    private List<VoterView> disagreeVoters;

    @ApiModelProperty(value = "Not Yet Voter List")
    @JsonProperty("not_yet_voters")
    private List<VoterView> notYetVoters;

    @ApiModelProperty(value = "Pass Support Rate")
    @JsonProperty("pass_support_rate")
    private Double passSupportRate;

    @ApiModelProperty(value = "Agree Miner Produced Block Count")
    @JsonProperty("agree_blocks_sum")
    private Long agreeBlocksSum;

    @ApiModelProperty(value = "Disagree Miner Produced Block Count")
    @JsonProperty("disagree_blocks_sum")
    private Long disagreeBlocksSum;

    @ApiModelProperty(value = "Effective Height")
    @JsonProperty("effective_height")
    private long effectiveHeight;

    @ApiModelProperty(value = "Effective Time")
    @JsonProperty("effective_time")
    private long effectiveTime;

    @ApiModelProperty(value = "Allow Vote: true-yes, false-no")
    @JsonProperty("allow_vote")
    private Boolean allowVote;

    @ApiModelProperty(value = "Support Rate")
    @JsonProperty("support_rate")
    private String supportRate;

    @ApiModelProperty(value = "Reject Rate")
    @JsonProperty("reject_rate")
    private String rejectRate;

}
