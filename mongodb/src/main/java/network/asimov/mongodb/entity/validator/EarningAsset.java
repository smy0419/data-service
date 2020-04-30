package network.asimov.mongodb.entity.validator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author sunmengyuan
 * @date 2019-11-15
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "earning_asset")
public class EarningAsset extends BaseEntity {

    /**
     * Earning Collection ID
     */
    @Field(value = "earning_id")
    private ObjectId earningId;

    /**
     * Earning Asset
     */
    private String asset;

    /**
     * Earning Amount
     */
    private Long value;

}
