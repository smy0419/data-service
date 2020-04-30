package network.asimov.mongodb.entity.miner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author zhangjing
 * @date 2019-09-25
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "miner_member")
public class Member extends BaseEntity {
    /**
     * Current Round
     */
    private Long round;

    /**
     * Member Address
     */
    private String address;

    /**
     * Mining Efficiency
     */
    private Integer efficiency;

    /**
     * Count Of Produced Block
     */
    private Long produced;
}
