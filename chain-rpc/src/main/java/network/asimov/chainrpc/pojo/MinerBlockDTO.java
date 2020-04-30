package network.asimov.chainrpc.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-09-30
 */

@Data
@Builder
public class MinerBlockDTO {
    private Long plannedBlocks;
    private Long actualBlocks;
}
