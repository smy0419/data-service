package network.asimov.mongodb.entity.validator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "earning")
public class Earning extends BaseEntity {
    /**
     * Transaction Hash
     */
    @Field(value = "tx_hash")
    private String txHash;

    /**
     * BTC Miner Address
     */
    @Field(value = "btc_miner_address")
    private String btcMinerAddress;

    /**
     * Beneficiary Address
     */
    private String address;

}
