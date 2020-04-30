package network.asimov.mongodb.service.dorg;

import network.asimov.mongodb.entity.dorg.TodoList;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Service("daoTodoListService")
public class TodoListService extends BaseService {
    public Pair<Long, List<TodoList>> listMyTodoList(String contractAddress, String operator, Integer todoType, boolean operated, Integer index, Integer limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").is(contractAddress)
                .and("operator").is(operator)
                .and("todo_type").is(todoType)
                .and("operated").is(operated));
        return queryByPage(index, limit, query, TodoList.class, Sort.Direction.DESC, "time");
    }

    public List<TodoList> listTodoByProposalId(long proposalId, String contractAddress) {
        Query query = new Query(
                Criteria.where("todo_id").is(proposalId).and("contract_address").is(contractAddress)
        );
        return mongoTemplate.find(query, TodoList.class);
    }
}
