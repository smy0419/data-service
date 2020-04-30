package network.asimov.chainrpc.pojo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-09-26
 */
@Data
@Builder
public class ActionDTO {
    private List<Integer> proposal;
    private List<Integer> vote;
}
