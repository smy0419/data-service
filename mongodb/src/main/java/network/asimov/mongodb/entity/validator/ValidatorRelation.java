package network.asimov.mongodb.entity.validator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author sunmengyuan
 * @date 2019-11-27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "validator_relation")
public class ValidatorRelation extends BaseEntity {
    /**
     * BTC Miner Address
     */
    @Field(value = "btc_miner_address")
    private String btcMinerAddress;

    /**
     * Bind Status
     */
    private Boolean bind;
    /**
     * Validator Address
     */
    private String address;
}
