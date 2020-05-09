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
 * @date 2020-04-03
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class VoteControllerTest {
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

        // insert proposal operation
        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 1L)
                .set(Tables.T_MINER_OPERATION.ROUND, 1L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash333")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 3)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":1111,\"decision\":true}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 2L)
                .set(Tables.T_MINER_OPERATION.ROUND, 1L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash444")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 3)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":2222,\"decision\":false}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
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
        p2.setType(1);
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
        todo1.setOperated(true);

        TodoList todo2 = new TodoList();
        todo2.setActionId(1111L);
        todo2.setOperator("0x6684eb1e592de5f92059f6c7766a04f2c9fd5587ad");
        todo2.setOperated(true);

        TodoList todo3 = new TodoList();
        todo3.setActionId(1111L);
        todo3.setOperator("0x66a8c1def9e07589415ebf59b55f76b9d5115064c9");
        todo3.setOperated(false);

        Vote v1 = new Vote();
        v1.setDecision(true);
        v1.setProposalId(1111L);
        v1.setTxHash("hash333");
        v1.setVoter("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a");
        v1.setTime(now);
        v1.setRound(1L);

        Vote v2 = new Vote();
        v2.setDecision(false);
        v2.setProposalId(2222L);
        v2.setTxHash("hash444");
        v2.setVoter("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a");
        v2.setTime(now);
        v2.setRound(1L);

        mongoTemplate.save(p);
        mongoTemplate.save(p2);
        mongoTemplate.save(todo1);
        mongoTemplate.save(todo2);
        mongoTemplate.save(todo3);
        mongoTemplate.save(v1);

        mongoTemplate.updateFirst(new Query(Criteria.where("address").is("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")), Update.update("produced", 100), Member.class);
        mongoTemplate.updateFirst(new Query(Criteria.where("address").is("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")), Update.update("planed", 100), Member.class);
        mongoTemplate.updateFirst(new Query(Criteria.where("address").is("0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")), Update.update("efficiency", 100), Member.class);

    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_MINER_OPERATION).where(Tables.T_MINER_OPERATION.ID.in(1L, 2L)).execute();
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.in(1L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("proposal_id").in(1111L, 2222L)), Proposal.class);
        mongoTemplate.remove(new Query(Criteria.where("action_id").in(1111L)), TodoList.class);
        mongoTemplate.remove(new Query(Criteria.where("proposal_id").in(1111L, 2222L)), Vote.class);
    }

    @Test
    public void listMyVote() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/miner/vote/mine")
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

    @Test
    public void checkVote() throws Exception {
        String json1 = "{\n" +
                "  \"id\": 2222\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/miner/committee/vote/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
                .header("token", "0x66a8c1def9e07589415ebf59b55f76b9d5115064c9")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }

    @Test
    public void vote() throws Exception {
        String json1 = "{\n" +
                "  \"call_data\": \"0x1111\",\n" +
                "  \"decision\": false,\n" +
                "  \"proposal_id\": 1111\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/miner/committee/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
                .header("token", "0x66a8c1def9e07589415ebf59b55f76b9d5115064c9")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2402))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"call_data\": \"0x123\",\n" +
                "  \"decision\": true,\n" +
                "  \"proposal_id\": 2222\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/miner/committee/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
                .header("token", "0x66a8c1def9e07589415ebf59b55f76b9d5115064c9")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }
}