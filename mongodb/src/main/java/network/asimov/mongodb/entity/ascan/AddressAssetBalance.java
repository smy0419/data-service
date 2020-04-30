package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author sunmengyuan
 * @date 2019-11-07
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "address_asset_balance")
public class AddressAssetBalance extends BaseEntity {
    /**
     * Transaction Address
     */
    private String address;

    /**
     * Transaction Asset
     */
    private String asset;

    /**
     * Transaction Amount
     */
    private Long balance;
}
