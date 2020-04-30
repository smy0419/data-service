package network.asimov.controller.miner;

import network.asimov.mongodb.entity.miner.Proposal;
import network.asimov.mongodb.entity.miner.TodoList;
import network.asimov.mysql.database.Tables;
import network.asimov.util.TimeUtil;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
    long now = TimeUtil.currentSeconds();

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    protected DSLContext dSLContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, "0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "pink man")
                .set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

        Proposal p = new Proposal();
        p.setStatus(0);
        p.setEndTime(now - TimeUtil.SECONDS_OF_DAY * 7);
        p.setRound(1L);
        p.setType(0);
        p.setProposalId(1111L);
        p.setTxHash("hash111");
        p.setAddress("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a");
        p.setTime(now);
        p.setEffectiveHeight(123L);
        p.setEffectiveTime(now + TimeUtil.SECONDS_OF_DAY * 7);
        p.setSupportRate(66L);
        p.setRejectRate(-1L);

        Proposal p2 = new Proposal();
        p2.setStatus(0);
        p2.setEndTime(now + TimeUtil.SECONDS_OF_DAY * 7);
        p2.setRound(1L);
        p2.setType(0);
        p2.setProposalId(2222L);
        p2.setTxHash("hash222");
        p2.setAddress("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a");
        p2.setTime(now);
        p2.setEffectiveHeight(123L);
        p2.setEffectiveTime(now + TimeUtil.SECONDS_OF_DAY * 7);
        p2.setSupportRate(66L);
        p2.setRejectRate(-1L);

        TodoList todo1 = new TodoList();
        todo1.setActionId(1111L);
        todo1.setOperator("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a");
        todo1.setOperated(false);
        todo1.setTime(now);

        TodoList todo2 = new TodoList();
        todo2.setActionId(2222L);
        todo2.setOperator("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a");
        todo2.setOperated(false);
        todo2.setTime(now);


        mongoTemplate.save(p);
        mongoTemplate.save(p2);
        mongoTemplate.save(todo1);
        mongoTemplate.save(todo2);

    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.in(1L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("proposal_id").in(1111L, 2222L)), Proposal.class);
        mongoTemplate.remove(new Query(Criteria.where("action_id").in(1111L, 2222L)), TodoList.class);
    }

    @Test
    public void listTodo() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/miner/todo/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
                .header("token", "0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}