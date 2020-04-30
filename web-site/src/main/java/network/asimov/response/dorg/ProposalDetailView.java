package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;
import network.asimov.response.common.PersonView;

/**
 * @author sunmengyuan
 * @date 2020-01-07
 */
@Data
@Builder
public class ProposalDetailView {
    @ApiModelProperty(value = "Proposal ID")
    @JsonProperty("proposal_id")
    private long proposalId;

    @ApiModelProperty(value = "ProposalType: 0-create asset")
    @JsonProperty("proposal_type")
    private int proposalType;

    @ApiModelProperty(value = "Proposer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PersonView proposer;

    @ApiModelProperty(value = "Proposal Create Time")
    @JsonProperty("create_time")
    private long createTime;

    @ApiModelProperty(value = "Proposal End Time")
    @JsonProperty("end_time")
    private long endTime;

    @ApiModelProperty(value = "Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed")
    @JsonProperty("tx_status")
    private int txStatus;

    @ApiModelProperty(value = "Proposal Status: 0-ongoing 1、passed 2、not passed 3、expired")
    @JsonProperty("proposal_status")
    private int proposalStatus;

    @ApiModelProperty(value = "Agree Voters Count")
    @JsonProperty("agree_voters_count")
    private int agreeVotersCount;

    @ApiModelProperty(value = "Disagree Voters Count")
    @JsonProperty("disagree_voters")
    private int disagreeVotersCount;

    @ApiModelProperty(value = "Total Voters Count")
    @JsonProperty("total_voters_count")
    private int totalVotersCount;

    @ApiModelProperty(value = "Pass Support Rate")
    @JsonProperty("pass_support_rate")
    private Double passSupportRate;

    @ApiModelProperty(value = "Issue Asset")
    @JsonProperty("issue_asset")
    private AssetView issueAsset;

    @ApiModelProperty(value = "Allow Vote: true-yes, false-no")
    @JsonProperty("allow_vote")
    private Boolean allowVote;

    @ApiModelProperty(value = "My Vote Decision: true-agree, false-disagree, null-not vote")
    @JsonProperty("my_decision")
    private Boolean myDecision;
}
