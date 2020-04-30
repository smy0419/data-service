package network.asimov.mongodb.entity.foundation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Council Member
 *
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "foundation_member")
public class Member extends BaseEntity {
    /**
     * Member Address
     */
    private String address;

    /**
     * In Service Status
     */
    @Field(value = "in_service")
    private boolean inService;

}
