package network.asimov.mongodb.service.foundation;

import network.asimov.mongodb.entity.foundation.TodoList;
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
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TodoServiceTest extends TodoService {

    @Before
    public void setUp() throws Exception {
        TodoList t1 = new TodoList();
        t1.setOperator("addr1");
        t1.setOperated(false);
        t1.setTodoId(1L);
        t1.setProposalType(1);

        TodoList t2 = new TodoList();
        t2.setOperator("addr2");
        t2.setOperated(false);
        t2.setTodoId(1L);
        t2.setProposalType(1);

        TodoList t3 = new TodoList();
        t3.setOperator("addr3");
        t3.setOperated(true);
        t3.setTodoId(1L);
        t2.setProposalType(1);

        TodoList t4 = new TodoList();
        t4.setOperator("addr1");
        t4.setOperated(false);
        t4.setTodoId(2L);
        t4.setProposalType(0);

        TodoList t5 = new TodoList();
        t5.setOperator("addr2");
        t5.setOperated(true);
        t5.setTodoId(2L);
        t5.setProposalType(0);

        TodoList t6 = new TodoList();
        t6.setOperator("addr5");
        t6.setOperated(false);
        t6.setTodoId(3L);
        t6.setProposalType(1);

        mongoTemplate.save(t1);
        mongoTemplate.save(t2);
        mongoTemplate.save(t3);
        mongoTemplate.save(t4);
        mongoTemplate.save(t5);
        mongoTemplate.save(t6);

    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("operator").in("addr1", "addr2", "addr3", "addr5"));
        mongoTemplate.remove(query, TodoList.class);
    }

    @Test
    public void testListTodoByAddress() {
        Integer index = 1;
        Integer limit = 10;
        Integer type1 = 1;
        String addr1 = "addr1";
        Pair<Long, List<TodoList>> p1 = listTodoByAddress(type1, index, limit, addr1);
        Assert.assertEquals(1, p1.getRight().size());

        String addr2 = "addr2";
        Integer type2 = 0;
        Pair<Long, List<TodoList>> p2 = listTodoByAddress(type2, index, limit, addr2);
        Assert.assertEquals(0, p2.getRight().size());

        Pair<Long, List<TodoList>> p3 = listTodoByAddress(type1, index, limit, addr2);
        Assert.assertEquals(1, p3.getRight().size());

    }

    @Test
    public void testListTodoByProposalId() {
        long proposalId1 = 1;
        List<TodoList> list1 = listTodoByProposalId(proposalId1);
        Assert.assertTrue(list1.size() > 0);

        long proposalId2 = 2;
        List<TodoList> list2 = listTodoByProposalId(proposalId2);
        Assert.assertTrue(list2.size() > 0);

        long proposalId3 = 3;
        List<TodoList> list3 = listTodoByProposalId(proposalId3);
        Assert.assertTrue(list3.size() > 0);
    }

    @Test
    public void testListTodoByAddressAndProposalType() {
        String addr1 = "addr1";
        Integer type1 = 1;
        List<TodoList> list1 = listTodoByAddressAndProposalType(addr1, type1);
        Assert.assertEquals(1, list1.size());

        String addr2 = "addr2";
        Integer type2 = 0;
        List<TodoList> list2 = listTodoByAddressAndProposalType(addr2, type2);
        Assert.assertEquals(0, list2.size());

        List<TodoList> list3 = listTodoByAddressAndProposalType(addr2, type1);
        Assert.assertEquals(1, list3.size());
    }
}