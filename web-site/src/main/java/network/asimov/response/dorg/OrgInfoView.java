package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sunmengyuan
 * @date 2019-12-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgInfoView {
    @ApiModelProperty(value = "Contract Address")
    @JsonProperty(value = "contract_address")
    private String contractAddress;

    @ApiModelProperty(value = "Vote Contract Address")
    @JsonProperty(value = "vote_contract_address")
    private String voteContractAddress;

    @ApiModelProperty(value = "Organization Name")
    private String name;

    @ApiModelProperty(value = "Organization Logo")
    private String logo;

    @ApiModelProperty(value = "Member Count")
    @JsonProperty(value = "member_count")
    private Long memberCount;

    @ApiModelProperty(value = "Organization Status: 1.normal 2.closed 3.local initialize")
    private Integer status;

    @ApiModelProperty(value = "Role Type: 0-president；1-member")
    @JsonProperty(value = "role_type")
    private Integer roleType;

    @ApiModelProperty(value = "Join Organization Time")
    @JsonProperty(value = "join_time")
    private Long joinTime;
}
