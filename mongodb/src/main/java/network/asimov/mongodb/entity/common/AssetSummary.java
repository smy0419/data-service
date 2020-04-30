package network.asimov.mongodb.entity.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-09-26
 */
@Data
@Builder
public class AssetSummary {
    /**
     * Amount
     */
    private long value;

    /**
     * Asset String
     */
    private String asset;
}

