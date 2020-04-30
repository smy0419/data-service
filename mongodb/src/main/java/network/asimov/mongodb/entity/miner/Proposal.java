package network.asimov.mongodb.entity.miner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author zhangjing
 * @date 2019-09-24
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "miner_proposal")
public class Proposal extends BaseEntity {
    /**
     * Current Round
     */
    private Long round;

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
    @Field(value = "type")
    private Integer type;

    /**
     * Proposal Status
     */
    private Integer status;

    /**
     * Transaction Hash
     */
    @Field(value = "tx_hash")
    private String txHash;

    /**
     * Proposal Effective Height
     */
    @Field(value = "effective_height")
    private Long effectiveHeight;

    /**
     * Proposal Effective Time
     */
    @Field(value = "effective_time")
    private Long effectiveTime;

    /**
     * Support Rate
     */
    @Field(value = "support_rate")
    private Long supportRate;

    /**
     * Reject Rate
     */
    @Field(value = "reject_rate")
    private Long rejectRate;

    public enum Status {
        /**
         * OnGoing: OnGoing
         * Approved: Approved,but not't effective yet.
         * Reject: Reject
         * Effective: Effective
         */
        OnGoing, Approved, Rejected, Effective
    }

    public enum Type {
        /**
         * ConfirmAssetForFee: Confirm which currency will be used as the transaction fee
         */
        ConfirmAssetForFee
    }
}
