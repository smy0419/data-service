package network.asimov.response.dorg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-12-16
 */
@Data
@Builder
public class MemberListView {
    @ApiModelProperty(value = "Member List")
    private List<MemberInfoView> members;
}
