package network.asimov.controller.dorg;

import network.asimov.mongodb.entity.dorg.Proposal;
import network.asimov.mongodb.entity.dorg.TodoList;
import network.asimov.mongodb.entity.dorg.Vote;
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
 * @date 2020-03-31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TaskControllerTest {
    long now = TimeUtil.currentSeconds();
    private String contractAddress = "0x6387a3b8a6b78b18d2984ef1f9c7acf8c39be0b515";

    private String contractCreator = "0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812";

    // indivisible asset
    private String asset1 = "000000010000000100000001";
    // divisible asset
    private String asset2 = "000000000000000100000001";


    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private DSLContext dSLContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        String additionalInfo = "{" +
                "\"asset\": \"" + asset1 + "\"," +
                "\"asset_name\": \"qq\"," +
                "\"asset_logo\": \"sss\"," +
                "\"asset_symbol\": \"s1\"," +
                "\"issue_amount\": 1" +
                "}";
        dSLContext.insertInto(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.ID, 1L)
                .set(Tables.T_DAO_OPERATION.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_OPERATION.TX_HASH, "hash1")
                .set(Tables.T_DAO_OPERATION.OPERATION_TYPE, (byte) 5)
                .set(Tables.T_DAO_OPERATION.ADDITIONAL_INFO, additionalInfo)
                .set(Tables.T_DAO_OPERATION.OPERATOR, contractCreator)
                .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) 1)
                .set(Tables.T_DAO_OPERATION.CREATE_TIME, now)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, now)
                .execute();

        String additionalInfo2 = "{" +
                "\"asset\": \"" + asset2 + "\"," +
                "\"asset_name\": \"qq2\"," +
                "\"asset_logo\": \"sss2\"," +
                "\"asset_symbol\": \"s2\"," +
                "\"issue_amount\": 12300000000" +
                "}";
        dSLContext.insertInto(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.ID, 2L)
                .set(Tables.T_DAO_OPERATION.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_OPERATION.TX_HASH, "hash2")
                .set(Tables.T_DAO_OPERATION.OPERATION_TYPE, (byte) 5)
                .set(Tables.T_DAO_OPERATION.ADDITIONAL_INFO, additionalInfo2)
                .set(Tables.T_DAO_OPERATION.OPERATOR, contractCreator)
                .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) 1)
                .set(Tables.T_DAO_OPERATION.CREATE_TIME, now)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, contractCreator)
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "pink man")
                .set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_INDIVISIBLE_ASSET)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ID, 1L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.TX_HASH, "xxxx")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET, asset1)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID, 1L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_DESC, "desc111")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.UPDATE_TIME, now)
                .execute();

        Proposal p1 = new Proposal();
        p1.setAddress(contractCreator);
        p1.setProposalId(1L);
        p1.setTxHash("hash1");
        p1.setContractAddress(contractAddress);
        p1.setEndTime(now + 7 * TimeUtil.SECONDS_OF_DAY);
        p1.setStatus(0);
        p1.setProposalType(0);
        p1.setTime(now);


        Proposal p2 = new Proposal();
        p2.setAddress(contractCreator);
        p2.setProposalId(2L);
        p2.setTxHash("hash2");
        p2.setContractAddress(contractAddress);
        p2.setEndTime(now - 7 * TimeUtil.SECONDS_OF_DAY);
        p2.setStatus(0);
        p2.setProposalType(0);
        p2.setTime(now - 7 * TimeUtil.SECONDS_OF_DAY);

        Vote v1 = new Vote();
        v1.setVoter("another voter address");
        v1.setVoteId(1L);
        v1.setContractAddress(contractAddress);
        v1.setDecision(true);
        v1.setTxHash("vote_hash_1");

        Vote v2 = new Vote();
        v2.setVoter(contractCreator);
        v2.setVoteId(1L);
        v2.setContractAddress(contractAddress);
        v2.setDecision(false);
        v2.setTxHash("vote_hash_2");

        TodoList todo1 = new TodoList();
        todo1.setTodoType(2);
        todo1.setTodoId(1L);
        todo1.setOperated(false);
        todo1.setContractAddress(contractAddress);
        todo1.setOperator(contractCreator);
        todo1.setEndTime(now - 7 * TimeUtil.SECONDS_OF_DAY);

        TodoList todo2 = new TodoList();
        todo2.setTodoType(2);
        todo2.setTodoId(2L);
        todo2.setOperated(true);
        todo2.setContractAddress(contractAddress);
        todo2.setOperator(contractCreator);
        todo2.setEndTime(now + 7 * TimeUtil.SECONDS_OF_DAY);

        mongoTemplate.save(p1);
        mongoTemplate.save(p2);
        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
        mongoTemplate.save(todo1);
        mongoTemplate.save(todo2);
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_OPERATION).where(Tables.T_DAO_OPERATION.ID.in(1L, 2L)).execute();
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.eq(1L)).execute();
        dSLContext.delete(Tables.T_DAO_INDIVISIBLE_ASSET).where(Tables.T_DAO_INDIVISIBLE_ASSET.ID.eq(1L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("contract_address").is(contractAddress)), Proposal.class);
        mongoTemplate.remove(new Query(Criteria.where("contract_address").is(contractAddress)), Vote.class);
        mongoTemplate.remove(new Query(Criteria.where("contract_address").is(contractAddress)), TodoList.class);
    }

    @Test
    public void toVoteTask() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/task/list/tovote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void votedTask() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/task/list/voted")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
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
    public void queryVote() throws Exception {
        String json1 = "{\n" +
                "  \"id\":\"" + 1 + "\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/task/vote/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"id\":\"" + 1111 + "\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/task/vote/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json3 = "{\n" +
                "  \"id\":\"" + 2 + "\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/task/vote/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}