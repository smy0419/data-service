package network.asimov.mongodb.entity.foundation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Proposal
 *
 * @author sunmengyuan
 * @date 2019-09-23
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "foundation_proposal")
public class Proposal extends BaseEntity {
    /**
     * Proposal End Time
     */
    @Field(value = "end_time")
    private Long endTime;

    /**
     * Proposal ID
     */
    @Field(value = "proposal_id")
    private Long proposalId;

    /**
     * Proposal Sponsor Address
     */
    private String address;

    /**
     * Proposal Type
     */
    @Field(value = "proposal_type")
    private Integer proposalType;

    /**
     * Proposal Status
     */
    private Integer status;

    /**
     * Transaction Hash
     */
    @Field(value = "tx_hash")
    private String txHash;

    public enum Status {
        /**
         * OnGoing: OnGoing
         * Approved: Approved
         * Reject: Expired (No results are cast before the expiry time)
         */
        OnGoing, Approved, Rejected
    }

    public enum Type {
        /**
         * Elect: Elect Director
         * Impeach: Impeach Director
         * Expenses: Project Investment
         */
        Elect,
        Impeach,
        Expenses,
    }
}
