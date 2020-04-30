package network.asimov.controller.foundation;

import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.entity.foundation.Vote;
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
    public void setUp() {
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
        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 1L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash111")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 2)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":2222,\"decision\":true}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "user1")
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 2L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash222")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 2)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":2222,\"decision\":true}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "user1")
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 3L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash333")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 1)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{\"comment\":\"some comments\"}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "user1")
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();

        Proposal p = new Proposal();
        p.setStatus(0);
        p.setEndTime(now - TimeUtil.SECONDS_OF_DAY * 7);
        p.setProposalId(1111L);
        p.setTxHash("hash333");
        p.setAddress("user1");
        p.setTime(now);
        p.setProposalType(0);

        Proposal p2 = new Proposal();
        p2.setStatus(0);
        p2.setEndTime(now + TimeUtil.SECONDS_OF_DAY * 7);
        p2.setProposalId(2222L);
        p2.setTxHash("hash444");
        p2.setAddress("user1");
        p2.setTime(now);
        p2.setProposalType(0);

        Vote v1 = new Vote();
        v1.setDecision(true);
        v1.setProposalId(1111L);
        v1.setTxHash("hash111");
        v1.setVoter("user1");
        v1.setTime(now);

        Vote v2 = new Vote();
        v2.setDecision(false);
        v2.setProposalId(2222L);
        v2.setTxHash("hash222");
        v2.setVoter("user1");
        v2.setTime(now);

        mongoTemplate.save(p);
        mongoTemplate.save(p2);
        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.eq(1L)).execute();
        dSLContext.delete(Tables.T_FOUNDATION_OPERATION).where(Tables.T_FOUNDATION_OPERATION.ID.in(1L, 2L, 3L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("proposal_id").in(1111L, 2222L)), Proposal.class);
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
        mvc.perform(MockMvcRequestBuilders.post("/foundation/vote/mine")
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
    }

    @Test
    public void checkVote() throws Exception {
        String json1 = "{\n" +
                "  \"decision\": false,\n" +
                "  \"proposal_id\": 2222\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/foundation/vote/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x662306d43258293846ab198810879e31feac759fac")
                .content(json1.getBytes())
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
                "  \"call_data\": \"0xqqq\",\n" +
                "  \"decision\": false,\n" +
                "  \"proposal_id\": 2222\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/foundation/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x662306d43258293846ab198810879e31feac759fac")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"call_data\": \"0xqqq\",\n" +
                "  \"decision\": false,\n" +
                "  \"proposal_id\": 2222\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/foundation/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user1")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}