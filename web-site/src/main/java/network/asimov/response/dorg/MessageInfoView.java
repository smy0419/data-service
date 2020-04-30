package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author sunmengyuan
 * @date 2019-12-17
 */
@Data
@Builder
public class MessageInfoView {
    @ApiModelProperty(value = "Message ID")
    private String id;

    @ApiModelProperty(value = "Message Category: 1.be member 2.add new member 3.create organization 4.been removed 5.remove member 6.be president 7.change president 8.transfer asset 9.issue asset 10.modify organization logo 11.modify organization name 12.new vote 13.proposal rejected 14.proposal expired 15.invited 16.close organization 17.receive asset 18.mint asset")
    private Integer category;

    @ApiModelProperty(value = "Message Type: 1.readonly 2.direct execute 3.vote")
    private Integer type;

    @ApiModelProperty(value = "Message Scope: 0.official website 1.dao organization 2.all")
    @JsonProperty("message_position")
    private Integer messagePosition;

    @ApiModelProperty(value = "Contract Address")
    @JsonProperty("contract_address")
    private String contractAddress;

    @ApiModelProperty(value = "Organization Name")
    @JsonProperty("org_name")
    private String orgName;

    @ApiModelProperty(value = "Vote Contract Address")
    @JsonProperty("vote_contract_address")
    private String voteContractAddress;

    @ApiModelProperty(value = "Additional Info")
    @JsonProperty("additional_info")
    private Map<String, Object> additionalInfo;

    @ApiModelProperty(value = "Message Status: 1.unread 2.read 3.disagree 4.agree")
    private Integer state;

    @ApiModelProperty(value = "Message Create Time")
    @JsonProperty("create_time")
    private Long createTime;
}
