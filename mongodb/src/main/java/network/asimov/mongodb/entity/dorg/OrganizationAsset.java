package network.asimov.mongodb.entity.dorg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Organization Asset
 *
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "dao_organization_asset")
public class OrganizationAsset extends BaseEntity {
    @Field(value = "contract_address")
    private String contractAddress;

    private String asset;

    @Field(value = "asset_type")
    private Integer assetType;

    @Field(value = "asset_index")
    private Integer assetIndex;
}
