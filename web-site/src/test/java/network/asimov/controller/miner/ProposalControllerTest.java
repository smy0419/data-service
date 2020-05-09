package network.asimov.controller.miner;

import network.asimov.mongodb.entity.miner.Member;
import network.asimov.mongodb.entity.miner.Proposal;
import network.asimov.mongodb.entity.miner.TodoList;
import network.asimov.mongodb.entity.miner.Vote;
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
import org.springframework.data.mongodb.core.query.Update;
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
public class ProposalControllerTest {
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
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, "user1")
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "pink man")
                .set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

        // insert proposal operation
        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 1L)
                .set(Tables.T_MINER_OPERATION.ROUND, 1L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash111")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 1)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{\"asset\":\"000000000000000000000000\"}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "user1")
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 2L)
                .set(Tables.T_MINER_OPERATION.ROUND, 1L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash222")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 1)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{\"asset\":\"000000000000000000000000\"}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "user1")
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();

        Proposal p = new Proposal();
        p.setStatus(0);
        p.setEndTime(now - TimeUtil.SECONDS_OF_DAY * 7);
        p.setRound(1L);
        p.setType(0);
        p.setProposalId(1L);
        p.setTxHash("hash111");
        p.setAddress("user1");
        p.setTime(now);
        p.setEffectiveHeight(123L);
        p.setEffectiveTime(now + TimeUtil.SECONDS_OF_DAY * 7);
        p.setSupportRate(66L);
        p.setRejectRate(-1L);

        TodoList todo1 = new TodoList();
        todo1.setActionId(1L);
        todo1.setOperator("user1");
        todo1.setOperated(true);

        TodoList todo2 = new TodoList();
        todo2.setActionId(1L);
        todo2.setOperator("user2");
        todo2.setOperated(true);

        TodoList todo3 = new TodoList();
        todo3.setActionId(1L);
        todo3.setOperator("user3");
        todo3.setOperated(false);

        Vote v1 = new Vote();
        v1.setDecision(true);
        v1.setProposalId(1L);
        v1.setTxHash("hash333");
        v1.setVoter("user1");
        v1.setTime(now);

        Vote v2 = new Vote();
        v2.setDecision(false);
        v2.setProposalId(1L);
        v2.setTxHash("hash444");
        v2.setVoter("user2");
        v2.setTime(now);

        Member m1 = new Member();
        m1.setEfficiency(100);
        m1.setProduced(111L);
        m1.setAddress("user1");
        m1.setRound(1L);

        Member m2 = new Member();
        m2.setEfficiency(99);
        m2.setProduced(88L);
        m2.setAddress("user2");
        m2.setRound(1L);

        Member m3 = new Member();
        m3.setEfficiency(0);
        m3.setProduced(0L);
        m3.setAddress("user3");
        m3.setRound(1L);

        mongoTemplate.save(p);
        mongoTemplate.save(todo1);
        mongoTemplate.save(todo2);
        mongoTemplate.save(todo3);
        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
        mongoTemplate.save(m1);
        mongoTemplate.save(m2);
        mongoTemplate.save(m3);

        mongoTemplate.updateFirst(new Query(Criteria.where("address").is("user1")), Update.update("produced", 100), Member.class);
        mongoTemplate.updateFirst(new Query(Criteria.where("address").is("user1")), Update.update("planed", 100), Member.class);
        mongoTemplate.updateFirst(new Query(Criteria.where("address").is("user1")), Update.update("efficiency", 100), Member.class);
    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_MINER_OPERATION).where(Tables.T_MINER_OPERATION.ID.in(1L, 2L)).execute();
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.in(1L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("proposal_id").is(1)), Proposal.class);
        mongoTemplate.remove(new Query(Criteria.where("address").in("user1", "user2", "user3")), Member.class);
        mongoTemplate.remove(new Query(Criteria.where("action_id").is(1)), TodoList.class);
        mongoTemplate.remove(new Query(Criteria.where("proposal_id").is(1)), Vote.class);
    }

    @Test
    public void listHistoryProposal() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/miner/proposal/history")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
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
    public void listMyProposal() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/miner/proposal/mine")
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
    public void getProposalById() throws Exception {
        String json1 = "{\n" +
                "  \"id\": 1\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/miner/proposal/info")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user1")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"id\": -1\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/miner/proposal/info")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user1")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void checkSubmit() throws Exception {
        String json1 = "{\n" +
                "  \"asset\": \"000000000000000300000001\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/miner/committee/proposal/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66a8c1def9e07589415ebf59b55f76b9d5115064c9")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2413))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void submitProposal() throws Exception {
        String json1 = "{\n" +
                "  \"call_data\": \"0x12123\",\n" +
                "  \"asset\": \"000000000000000300000001\",\n" +
                "  \"comment\": \"qqqqq\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/miner/committee/proposal/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user1")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        mvc.perform(MockMvcRequestBuilders.post("/miner/committee/proposal/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user111")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }
}