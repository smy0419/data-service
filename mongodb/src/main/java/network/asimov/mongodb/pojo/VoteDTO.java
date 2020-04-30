package network.asimov.mongodb.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-09-29
 */
@Data
@Builder
public class VoteDTO {
    /**
     * Current Round
     */
    private Long round;

    /**
     * Proposal ID
     */
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
     * Vote Transaction Hash
     */
    private String txHash;

    /**
     * ProposalTransaction Hash
     */
    private String proposalTxHash;

    /**
     * Proposal Type
     */
    private Integer type;

    /**
     * Proposal Time
     */
    private Long time;

    /**
     * Proposal Status
     */
    private Integer status;

    /**
     * Proposal Sponsor Address
     */
    private String address;
}
