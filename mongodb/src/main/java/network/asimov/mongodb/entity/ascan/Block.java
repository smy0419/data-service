package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import network.asimov.mongodb.entity.common.AssetSummary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-09-26
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "block")
public class Block extends BaseEntity {
    /**
     * Block Hash
     */
    private String hash;

    /**
     * Count Of Block Confirmed
     */
    private Long confirmations;

    /**
     * Block Size
     */
    private Integer size;

    /**
     * Version
     */
    private Integer version;

    /**
     * Transaction Merkle Root
     */
    @Field(value = "merkle_root")
    private String merkleRoot;

    /**
     * Transaction Count In Block
     */
    @Field(value = "tx_count")
    private Long txCount;

    /**
     * Previous Block Hash
     */
    @Field(value = "previous_block_hash")
    private String previousBlockHash;

    /**
     * StateDB Root
     */
    @Field(value = "state_root")
    private String stateRoot;

    /**
     * Block Produced Address
     */
    private String produced;

    /**
     * Mining Reword
     */
    private Long reward;

    /**
     * Fee Rewords
     */
    private List<AssetSummary> fee;

    /**
     * Generate Block Window / Generate Block time
     */
    private Integer slot;

    /**
     * Generate Block Cycle
     */
    private Integer round;
}
