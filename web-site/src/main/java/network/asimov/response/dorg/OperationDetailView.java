package network.asimov.response.dorg;

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
public class OperationDetailView {
    @ApiModelProperty(value = "New President")
    @JsonProperty("new_president")
    private PersonView newPresident;

    @ApiModelProperty(value = "Change Person")
    @JsonProperty("change_person")
    private PersonView changePerson;

    @ApiModelProperty(value = "Operation Type: 1.create organization 2.close organization 3.modify organization name 4.remove member 5.create asset 6.transfer asset 7.president transfer 8.president confirm 9.invite member 10.join organization 11.vote 12.modify organization logo 13.mint asset")
    @JsonProperty("operation_type")
    private Integer operationType;

    @ApiModelProperty(value = "Operator")
    private String operator;

    @ApiModelProperty(value = "Asset")
    private AssetView asset;

    @ApiModelProperty(value = "Organization Information")
    @JsonProperty("org_info")
    private OrgInfoView orgInfo;

    @ApiModelProperty(value = "Transaction Status: 0.unconfirmed，1.transaction confirmed, contract execution success 2.transaction confirmed, contract execution failed 3.local action")
    @JsonProperty("tx_status")
    private int txStatus;

    @ApiModelProperty(value = "Create Time")
    @JsonProperty("create_time")
    private Long createTime;

    @ApiModelProperty(value = "Update Time")
    @JsonProperty("update_time")
    private Long updateTime;
}
