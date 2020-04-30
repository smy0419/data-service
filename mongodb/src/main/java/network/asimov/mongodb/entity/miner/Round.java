package network.asimov.mongodb.entity.miner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author zhangjing
 * @date 2019-09-25
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "miner_round")
public class Round extends BaseEntity {
    /**
     * Current Round
     */
    private Long round;

    /**
     * Round Start Time
     */
    @Field(value = "start_time")
    private Long startTime;

    /**
     * Round End Time
     */
    @Field(value = "end_time")
    private Long endTime;
}
