package network.asimov.mongodb.entity.dorg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Organization
 *
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "dao_organization")
public class Organization extends BaseEntity {
    @Field(value = "tx_hash")
    private String txHash;

    @Field(value = "contract_address")
    private String contractAddress;

    @Field(value = "vote_contract_address")
    private String voteContractAddress;

    @Field(value = "vote_template_name")
    private String voteTemplateName;

    private String president;

    @Field(value = "org_name")
    private String orgName;

    @Field(value = "org_id")
    private Integer orgId;

    private Integer status;

    public enum Status {
        /**
         * Normal: Normal
         * Closed: Closed
         * Init: Init, but not in chain.
         */
        Normal, Closed, Init,
    }

}
