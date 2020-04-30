package network.asimov.mongodb.entity.miner;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author sunmengyuan
 * @date 2019-09-24
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "miner_todo_list")
public class TodoList extends BaseEntity {
    /**
     * Current Round
     */
    private Long round;

    /**
     * Operator
     */
    private String operator;

    /**
     * To-Do Proposal ID
     */
    @Field(value = "action_id")
    private Long actionId;

    /**
     * To-Do Proposal Type
     */
    @Field(value = "action_type")
    private Integer actionType;

    /**
     * To-Do End Time == Proposal End Time
     */
    @Field(value = "end_time")
    private Long endTime;

    /**
     * Operated Status
     */
    private Boolean operated;
}
