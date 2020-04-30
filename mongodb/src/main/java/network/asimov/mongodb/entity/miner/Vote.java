package network.asimov.mongodb.entity.miner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author zhangjing
 * @date 2019-09-25
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "miner_vote")
public class Vote extends BaseEntity {
    /**
     * Current Round
     */
    private Long round;

    /**
     * Proposal ID
     */
    @Field(value = "proposal_id")
    private Long proposalId;

    /**
     * Voter
     */
    private String voter;

    /**
     * Vote Decision
     */
    private Boolean decision;

    /**
     * Transaction Hash
     */
    @Field(value = "tx_hash")
    private String txHash;
}
