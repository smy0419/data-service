package network.asimov.response.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-01-10
 */
@Data
@Builder
public class OverView {
    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Unread Message Count")
    private Long unread;

    @ApiModelProperty(value = "My Organization List")
    private List<OrgInfoView> orgs;

    @ApiModelProperty(value = "Message List")
    private List<MessageInfoView> messages;
}
