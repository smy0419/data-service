package network.asimov.mongodb.service.foundation;

import network.asimov.mongodb.entity.foundation.TodoList;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-10-15
 */
@Service("foundationTodoService")
public class TodoService extends BaseService {
    /**
     * Paging to get to-do list via address
     *
     * @param address operator address
     * @return <total count, to-do list >
     */
    public Pair<Long, List<TodoList>> listTodoByAddress(Integer proposalType, Integer index, Integer limit, String address) {
        Query query = new Query();
        Criteria criteria = Criteria.where("operator").is(address);
        if (proposalType != null) {
            criteria.and("proposal_type").is(proposalType);
        }
        criteria.and("operated").is(false);
        query.addCriteria(criteria);
        return queryByPage(index, limit, query, TodoList.class, Sort.Direction.DESC, "time");
    }

    /**
     * List to-do list via proposal id
     *
     * @param proposalId proposal id
     * @return to-do list
     */
    public List<TodoList> listTodoByProposalId(long proposalId) {
        Query query = new Query(
                Criteria.where("todo_id").is(proposalId)
        );
        return mongoTemplate.find(query, TodoList.class);
    }

    /**
     * List to-do list via address and proposal type
     *
     * @param address      operator address
     * @param proposalType proposal type
     * @return
     */
    public List<TodoList> listTodoByAddressAndProposalType(String address, Integer proposalType) {
        Query query = new Query(
                Criteria.where("operator").is(address)
                        .and("proposal_type").is(proposalType)
                        .and("operated").is(false)
        );
        return mongoTemplate.find(query, TodoList.class);
    }
}
