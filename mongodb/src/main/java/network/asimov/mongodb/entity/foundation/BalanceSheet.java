package network.asimov.mongodb.entity.foundation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Found Flow Record
 *
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "foundation_balance_sheet")
public class BalanceSheet extends BaseEntity {
    private String address;
    private String asset;
    private Long amount;

    @Field(value = "transfer_type")
    private Integer transferType;

    @Field(value = "proposal_type")
    private Integer proposalType;

    @Field(value = "tx_hash")
    private String txHash;
}
