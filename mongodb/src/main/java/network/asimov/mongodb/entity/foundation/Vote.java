package network.asimov.mongodb.entity.foundation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Vote
 *
 * @author sunmengyuan
 * @date 2019-09-23
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "foundation_vote")
public class Vote extends BaseEntity {
    /**
     * Proposal ID
     */
    @Field(value = "proposal_id")
    private long proposalId;

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
