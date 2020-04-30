package network.asimov.mongodb.entity.dorg;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Organization Member
 *
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "dao_member")
public class Member extends BaseEntity {
    @Field(value = "contract_address")
    private String contractAddress;

    private Integer role;

    private String address;

    private Integer status;

    public enum Status {
        /**
         * Invited: Be invited, but unconfirmed
         * Agreed: Agreed to join
         * Removed: Have been removed
         */
        Invited, Agreed, Removed
    }

    public enum Role {
        /**
         * President: 主席
         * Ordinary: 普通成员
         */
        President, Ordinary
    }
}
