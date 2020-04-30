package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import network.asimov.mongodb.entity.common.AssetSummary;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author zhangjing
 * @date 2020/4/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class TransactionList extends BaseEntity {
    /**
     * Address or Contract or Asset
     */
    private String key;

    /**
     * Transaction Hash
     */
    @Field(value = "tx_hash")
    private String txHash;

    /**
     * Transaction Fee
     */
    private List<AssetSummary> fee;
}
