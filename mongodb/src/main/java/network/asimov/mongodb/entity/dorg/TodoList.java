package network.asimov.mongodb.entity.dorg;

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
@Document(collection = "dao_todo_list")
public class TodoList extends BaseEntity {
    @Field(value = "contract_address")
    private String contractAddress;

    private String operator;

    @Field(value = "todo_type")
    private Integer todoType;

    @Field(value = "todo_id")
    private Long todoId;

    @Field(value = "end_time")
    private Long endTime;

    private Boolean operated;

    public enum Type {
        /**
         * InviteMember: Invite to be a member for confirm
         * InvitePresident: Invite to be a president for confirm
         * Vote: Vote
         */
        InviteMember,
        InvitePresident,
        Vote,
    }

}