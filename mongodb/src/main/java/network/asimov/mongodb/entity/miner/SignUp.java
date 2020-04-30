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
@Document(collection = "miner_sign_up")
public class SignUp extends BaseEntity {
    /**
     * Current Round
     */
    private Long round;

    /**
     * Transaction Hash
     */
    @Field(value = "tx_hash")
    private String txHash;

    /**
     * Sign UP Address
     */
    private String address;

    /**
     * Mining Efficiency
     */
    private Integer efficiency;

    /**
     * Count Of Produced Blocks
     */
    private Long produced;
}
