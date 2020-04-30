package network.asimov.mongodb.service.dorg;

import network.asimov.mongodb.entity.dorg.TodoList;
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
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TodoListServiceTest extends TodoListService {

    @Before
    public void setUp() throws Exception {
        TodoList todo1 = new TodoList();
        todo1.setContractAddress("address1");
        todo1.setOperator("user1");
        todo1.setOperated(true);
        todo1.setTodoId(1L);
        todo1.setTodoType(2);

        TodoList todo2 = new TodoList();
        todo2.setContractAddress("address1");
        todo2.setOperator("user1");
        todo2.setOperated(false);
        todo2.setTodoId(2L);
        todo2.setTodoType(1);


        TodoList todo3 = new TodoList();
        todo3.setContractAddress("address1");
        todo3.setOperator("user1");
        todo3.setOperated(true);
        todo3.setTodoId(3L);
        todo3.setTodoType(0);

        TodoList todo4 = new TodoList();
        todo4.setContractAddress("address2");
        todo4.setOperator("user1");
        todo4.setOperated(true);
        todo4.setTodoId(3L);
        todo4.setTodoType(2);

        TodoList todo5 = new TodoList();
        todo5.setContractAddress("address2");
        todo5.setOperator("user2");
        todo5.setOperated(true);
        todo5.setTodoId(3L);
        todo5.setTodoType(0);

        TodoList todo6 = new TodoList();
        todo6.setContractAddress("address1");
        todo6.setOperator("user3");
        todo6.setOperated(false);
        todo6.setTodoId(2L);
        todo6.setTodoType(1);

        mongoTemplate.save(todo1);
        mongoTemplate.save(todo2);
        mongoTemplate.save(todo3);
        mongoTemplate.save(todo4);
        mongoTemplate.save(todo5);
        mongoTemplate.save(todo6);

    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("contract_address").in("address1", "address2"));
        mongoTemplate.remove(query, TodoList.class);
    }

    @Test
    public void testListMyTodoList() {
        Integer index = 1;
        Integer limit = 10;

        String contractAddr1 = "address1";
        String operator1 = "user1";
        Pair<Long, List<TodoList>> p1 = listMyTodoList(contractAddr1, operator1, 1, false, index, limit);
        Assert.assertEquals(1, p1.getRight().size());

        String contractAddr2 = "address2";
        String operator2 = "user2";
        Pair<Long, List<TodoList>> p2 = listMyTodoList(contractAddr2, operator2, 1, false, index, limit);
        Assert.assertEquals(0, p2.getRight().size());
    }

    @Test
    public void testListTodoByProposalId() {
        List<TodoList> list1 = listTodoByProposalId(2L, "address1");
        List<String> addrList = list1.stream().map(TodoList::getOperator).collect(Collectors.toList());
        Assert.assertTrue(addrList.contains("user1"));
        Assert.assertTrue(addrList.contains("user3"));
    }
}