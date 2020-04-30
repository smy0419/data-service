package network.asimov.mongodb.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * @author zhangjing
 * @date 2019-09-20
 */

@Data
public class BaseEntity {
    @Id
    private ObjectId id;

    /**
     * Block Height
     */
    private Long height;

    /**
     * Record Time
     */
    private Long time;
}
