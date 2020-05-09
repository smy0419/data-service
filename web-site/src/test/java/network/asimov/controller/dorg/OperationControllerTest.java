package network.asimov.controller.dorg;

import network.asimov.mongodb.entity.dorg.Member;
import network.asimov.mongodb.entity.dorg.Proposal;
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
public class OperationControllerTest {
    private String contractAddress = "0x6387a3b8a6b78b18d2984ef1f9c7acf8c39be0b515";

    private String contractCreator = "0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812";

    long now = TimeUtil.currentSeconds();
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DSLContext dSLContext;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        dSLContext.insertInto(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.ID, 1L)
                .set(Tables.T_DAO_OPERATION.TX_HASH, "hash1")
                .set(Tables.T_DAO_OPERATION.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_OPERATION.OPERATION_TYPE, (byte) 5)
                .set(Tables.T_DAO_OPERATION.ADDITIONAL_INFO, "{\"asset\":\"000000000000000000000000\"}")
                .set(Tables.T_DAO_OPERATION.OPERATOR, contractCreator)
                .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) 1)
                .set(Tables.T_DAO_OPERATION.CREATE_TIME, now)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, now)
                .execute();

        Proposal proposal = new Proposal();
        proposal.setTxHash("hash1");
        proposal.setStatus(0);
        proposal.setEndTime(now - TimeUtil.SECONDS_OF_DAY);
        proposal.setProposalId(1L);
        proposal.setProposalType(2);
        proposal.setContractAddress(contractAddress);
        proposal.setAddress(contractCreator);

        Member m1 = new Member();
        m1.setContractAddress(contractAddress);
        m1.setAddress(contractCreator);
        m1.setRole(0);
        m1.setStatus(1);


        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, contractCreator)
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "pink man")
                .set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

        mongoTemplate.save(proposal);
        mongoTemplate.save(m1);

    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_OPERATION).where(Tables.T_DAO_OPERATION.ID.eq(1L)).execute();
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.eq(1L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("tx_hash").is("hash1")), Proposal.class);
        mongoTemplate.remove(new Query(Criteria.where("address").is(contractCreator)), Member.class);
    }

    @Test
    public void getTxStatusByHash() throws Exception {
        String json1 = "{}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/transaction/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : tx hash not blank"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\"tx_hash\":\"d8fda3131f627f247525195465c0d205220a6fa920184045939e751b12725df1\"}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/transaction/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json3 = "{\"tx_hash\":\"hash1\"}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/transaction/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listOperationRecord() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/operation/record/list")
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

}

