package network.asimov.controller.foundation;

import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.entity.foundation.TodoList;
import network.asimov.mysql.database.Tables;
import network.asimov.util.TimeUtil;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author sunmengyuan
 * @date 2020-04-02
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TodoControllerTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    long now = TimeUtil.currentSeconds();

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    protected DSLContext dSLContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, "user1")
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "name1")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

        TodoList todo1 = new TodoList();
        todo1.setTodoId(1L);
        todo1.setOperator("user1");
        todo1.setOperated(false);
        todo1.setProposalType(1);
        todo1.setTime(now);

        TodoList todo2 = new TodoList();
        todo2.setTodoId(2L);
        todo2.setOperator("user1");
        todo2.setOperated(false);
        todo2.setProposalType(0);
        todo2.setTime(now);

        TodoList todo3 = new TodoList();
        todo3.setTodoId(3L);
        todo3.setOperator("user1");
        todo3.setOperated(false);
        todo3.setProposalType(0);
        todo3.setTime(now);

        Proposal p = new Proposal();
        p.setStatus(0);
        p.setEndTime(now - TimeUtil.SECONDS_OF_DAY * 7);
        p.setProposalId(1L);
        p.setTxHash("hash111");
        p.setAddress("user1");
        p.setTime(now);
        p.setProposalType(1);

        Proposal p2 = new Proposal();
        p2.setStatus(0);
        p2.setEndTime(now + TimeUtil.SECONDS_OF_DAY * 7);
        p2.setProposalId(2L);
        p2.setTxHash("hash222");
        p2.setAddress("user1");
        p2.setTime(now);
        p2.setProposalType(0);

        mongoTemplate.save(todo1);
        mongoTemplate.save(todo2);

        mongoTemplate.save(p);
        mongoTemplate.save(p2);
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.eq(1L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("proposal_id").in(1L, 2L)), Proposal.class);
        mongoTemplate.remove(new Query(Criteria.where("todo_id").in(1L, 2L, 3L)), TodoList.class);
    }

    @Test
    public void listTodo1() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/foundation/todo/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user1")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listTodo2() throws Exception {

        TodoList todo3 = new TodoList();
        todo3.setTodoId(3L);
        todo3.setOperator("user1");
        todo3.setOperated(false);
        todo3.setProposalType(0);
        todo3.setTime(now);
        mongoTemplate.save(todo3);

        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/foundation/todo/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user1")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResolvedException();
    }

}