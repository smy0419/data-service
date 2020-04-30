package network.asimov.mongodb.entity.foundation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * To-Do List
 *
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "foundation_todo_list")
public class TodoList extends BaseEntity {

    /**
     * Operator
     */
    private String operator;

    /**
     * To-Do Proposal ID
     */
    @Field(value = "todo_id")
    private Long todoId;

    /**
     * To-Do Type，0-Vote，1-Confirm Proposal
     */
    @Field(value = "todo_type")
    private Integer todoType;

    /**
     * Proposal Type
     */
    @Field(value = "proposal_type")
    private Integer proposalType;

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