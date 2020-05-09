package network.asimov.controller.dorg;

import network.asimov.mongodb.entity.dorg.Organization;
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
public class MessageControllerTest {
    long now = TimeUtil.currentSeconds();

    private String contractAddress = "0x6387a3b8a6b78b18d2984ef1f9c7acf8c39be0b515";

    private String contractCreator = "0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    protected DSLContext dSLContext;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        String additionalInfo = "{" +
                "\"target_address\": \"" + contractCreator + "\"," +
                "\"new_president\": \"" + contractCreator + "\"," +
                "\"old_president\": \"" + contractCreator + "\"," +
                "\"asset\": \"000000000000000000000000\"" +
                "}";

        dSLContext.insertInto(Tables.T_DAO_MESSAGE)
                .set(Tables.T_DAO_MESSAGE.ID, 1L)
                .set(Tables.T_DAO_MESSAGE.CATEGORY, 1)
                .set(Tables.T_DAO_MESSAGE.TYPE, 1)
                .set(Tables.T_DAO_MESSAGE.MESSAGE_POSITION, 2)
                .set(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_MESSAGE.RECEIVER, contractCreator)
                .set(Tables.T_DAO_MESSAGE.ADDITIONAL_INFO, additionalInfo)
                .set(Tables.T_DAO_MESSAGE.STATE, 0)
                .set(Tables.T_DAO_MESSAGE.CREATE_TIME, now)
                .set(Tables.T_DAO_MESSAGE.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, contractCreator)
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "pink man")
                .set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

        Organization org = new Organization();
        org.setContractAddress(contractAddress);
        org.setStatus(0);
        org.setOrgName("org_name");
        org.setPresident(contractCreator);
        org.setTxHash("hash1");
        org.setOrgId(1);

        mongoTemplate.save(org);
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_MESSAGE).where(Tables.T_DAO_MESSAGE.ID.eq(1L)).execute();
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ADDRESS.eq(contractCreator)).execute();
        mongoTemplate.remove(new Query(Criteria.where("contract_address").is(contractAddress)), Organization.class);
    }

    @Test
    public void listMyMessage() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/message/list/mine")
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
    public void listOrgMessage() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/message/list/org")
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
    public void updateMessage() throws Exception {
        String json1 = "{\n" +
                "  \"message_id\": 1,\n" +
                "  \"state\": 1\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/message/update")
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

        String json2 = "{\n" +
                "  \"message_id\": 111,\n" +
                "  \"state\": 1\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/message/update")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2503))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}