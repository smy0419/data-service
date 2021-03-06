package network.asimov.mongodb.entity.dorg;

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
@Document(collection = "dao_vote")
public class Vote extends BaseEntity {
    @Field(value = "tx_hash")
    private String txHash;

    @Field(value = "contract_address")
    private String contractAddress;

    private String voter;

    @Field(value = "vote_id")
    private Long voteId;

    private Boolean decision;
}
