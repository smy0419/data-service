package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.TodoList;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-03-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TodoServiceTest extends TodoService {

    @Before
    public void setUp() throws Exception {
        TodoList todo1 = new TodoList();
        todo1.setActionId(1111L);
        todo1.setOperated(false);
        todo1.setOperator("addr1");
        todo1.setActionType(1);

        TodoList todo2 = new TodoList();
        todo2.setActionId(1111L);
        todo2.setOperated(false);
        todo2.setOperator("addr2");
        todo2.setActionType(1);

        TodoList todo3 = new TodoList();
        todo3.setActionId(1111L);
        todo3.setOperated(true);
        todo3.setOperator("addr3");
        todo3.setActionType(1);

        TodoList todo4 = new TodoList();
        todo4.setActionId(2222L);
        todo4.setOperated(false);
        todo4.setOperator("addr1");
        todo4.setActionType(2);

        TodoList todo5 = new TodoList();
        todo5.setActionId(2222L);
        todo5.setOperated(true);
        todo5.setOperator("addr3");
        todo5.setActionType(2);

        TodoList todo6 = new TodoList();
        todo6.setActionId(3333L);
        todo6.setOperated(true);
        todo6.setOperator("addr3");
        todo6.setActionType(1);

        mongoTemplate.save(todo1);
        mongoTemplate.save(todo2);
        mongoTemplate.save(todo3);
        mongoTemplate.save(todo4);
        mongoTemplate.save(todo5);
        mongoTemplate.save(todo6);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("operator").in("addr1", "addr2", "addr3")), TodoList.class);
    }

    @Test
    public void testListTodoByAddress() {
        Integer index = 1;
        Integer limit = 10;

        Pair<Long, List<TodoList>> p1 = listTodoByAddress(index, limit, "addr1");
        Assert.assertEquals(2, p1.getRight().size());

        Pair<Long, List<TodoList>> p2 = listTodoByAddress(index, limit, "addr3");
        Assert.assertEquals(0, p2.getRight().size());

    }

    @Test
    public void testCountTodo() {
        long count1 = countTodo("addr1", 1);
        long count2 = countTodo("addr1", 2);
        long count3 = countTodo("addr2", 1);
        long count4 = countTodo("addr2", 2);
        long count5 = countTodo("addr3", 1);
        long count6 = countTodo("addr3", 2);
        Assert.assertEquals(1, count1);
        Assert.assertEquals(1, count2);
        Assert.assertEquals(1, count3);
        Assert.assertEquals(0, count4);
        Assert.assertEquals(0, count5);
        Assert.assertEquals(0, count6);

    }

    @Test
    public void testListTodoByProposalId() {
        List<TodoList> list1 = listTodoByProposalId(1111);
        List<TodoList> list2 = listTodoByProposalId(2222);
        List<TodoList> list3 = listTodoByProposalId(3333);
        List<TodoList> list4 = listTodoByProposalId(4444);
        Assert.assertEquals(3, list1.size());
        Assert.assertEquals(2, list2.size());
        Assert.assertEquals(1, list3.size());
        Assert.assertEquals(0, list4.size());
    }
}