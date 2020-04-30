package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.TodoList;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-09-24
 */
@Service("minerTodoService")
public class TodoService extends BaseService {
    /**
     * Paging to get to-do list via address
     *
     * @param address operator address
     * @return <total count, to-do list>
     */
    public Pair<Long, List<TodoList>> listTodoByAddress(Integer index, Integer limit, String address) {
        Query query = new Query(Criteria.where("operator").is(address).and("operated").is(false));
        return queryByPage(index, limit, query, TodoList.class, Sort.Direction.DESC, "time");
    }

    /**
     * Count to-do list
     *
     * @param address      operator address
     * @param proposalType proposal type
     * @return to-do list count
     */
    public long countTodo(String address, Integer proposalType) {
        Query query = new Query(
                Criteria.where("operator").is(address)
                        .and("operated").is(false)
                        .and("action_type").is(proposalType)
        );
        return mongoTemplate.count(query, TodoList.class);
    }

    /**
     * List to-do via proposal id
     *
     * @param proposalId proposal id
     * @return to-do list
     */
    public List<TodoList> listTodoByProposalId(long proposalId) {
        Query query = new Query(
                Criteria.where("action_id").is(proposalId)
        );
        return mongoTemplate.find(query, TodoList.class);
    }

}
