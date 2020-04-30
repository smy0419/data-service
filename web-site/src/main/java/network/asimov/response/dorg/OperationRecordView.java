package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.PersonView;

import java.util.Map;

/**
 * @author sunmengyuan
 * @date 2019-12-17
 */
@Data
@Builder
public class OperationRecordView {
    @ApiModelProperty(value = "Operation ID")
    @JsonProperty("operation_id")
    private String operationId;

    @ApiModelProperty(value = "Operation Type: 1.create organization 2.close organization 3.modify organization name 4.remove member 5.create asset 6.transfer asset 7.president transfer 8.president confirm 9.invite member 10.join organization 11.vote 12.modify organization logo 13.mint asset")
    @JsonProperty("operation_type")
    private Integer operationType;

    @ApiModelProperty(value = "Additional Info")
    @JsonProperty("additional_info")
    private Map<String, Object> additionalInfo;

    @ApiModelProperty(value = "Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed 3.local action")
    @JsonProperty("tx_status")
    private Integer txStatus;

    @ApiModelProperty(value = "Proposal Status: -1-meaningless，0-vote in progress，1-vote passed，2-vote not passed，3：vote expired")
    @JsonProperty("proposal_status")
    private Integer proposalStatus;

    @ApiModelProperty(value = "Proposer")
    private PersonView proposer;

    @ApiModelProperty(value = "Proposer Rol: 0-president, 1-member")
    @JsonProperty("proposal_role")
    private Integer proposerRole;

    @ApiModelProperty(value = "Create Time")
    @JsonProperty("create_time")
    private Long createTime;

}
