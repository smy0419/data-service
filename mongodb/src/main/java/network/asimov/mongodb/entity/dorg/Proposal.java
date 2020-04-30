package network.asimov.mongodb.entity.dorg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author sunmengyuan
 * @date 2020-01-06
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "dao_proposal")
public class Proposal extends BaseEntity {
    /**
     * Transaction Hash
     */
    @Field(value = "tx_hash")
    private String txHash;

    /**
     * Dao Organization Contract Address
     */
    @Field(value = "contract_address")
    private String contractAddress;

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


    public enum Status {
        /**
         * OnGoing: OnGoing
         * Approved: Approved
         * Reject: Reject
         * Expired: Expired (No results are cast before the expiry time)
         */
        OnGoing, Approved, Rejected, Expired
    }

    public enum Type {
        /**
         * IssueAsset: Mint Asset
         */
        IssueAsset,
    }
}
