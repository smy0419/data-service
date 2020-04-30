package network.asimov.mongodb.entity.ascan;

import lombok.*;
import network.asimov.mongodb.entity.BaseEntity;
import network.asimov.mongodb.entity.common.AssetSummary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-28
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "transaction")
public class Transaction extends BaseEntity {
    /**
     * Block Hash
     */
    @Field(value = "block_hash")
    private String blockHash;

    /**
     * Transaction Hex
     */
    private String hex;

    /**
     * Transaction Hash
     */
    private String hash;

    /**
     * Virtual Transaction Hash
     */
    @Field(value = "vtx_hash")
    private String vtxHash;

    /**
     * Transaction size
     */
    private Integer size;

    /**
     * Transaction Version
     */
    private Long version;

    /**
     * Lock Time
     */
    @Field(value = "lock_time")
    private Long lockTime;

    /**
     * Count Of Transaction Confirmed
     */
    private Long confirmations;

    /**
     * Transaction fee
     */
    private List<AssetSummary> fee;

    private List<Vin> vin;

    private List<Vout> vout;

    /**
     * Gas Limit
     */
    @Field(value = "gas_limit")
    private Long gasLimit;

    @AllArgsConstructor
    public enum TxStatus {
        /**
         * Normal Transaction
         */
        Normal(1),

        /**
         * Virtual Transaction
         */
        Virtual(2);

        @Getter
        private int code;
    }
}
