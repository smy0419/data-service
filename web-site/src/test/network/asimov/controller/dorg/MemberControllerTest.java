package network.asimov.controller.dorg;

import network.asimov.mongodb.entity.dorg.Member;
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
public class MemberControllerTest {
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

        Member m1 = new Member();
        m1.setStatus(1);
        m1.setRole(0);
        m1.setContractAddress(contractAddress);
        m1.setAddress(contractCreator);

        Organization org = new Organization();
        org.setContractAddress(contractAddress);
        org.setPresident(contractCreator);
        org.setStatus(0);
        org.setOrgName("org1");
        org.setOrgId(1);
        org.setTxHash("hash1");

        mongoTemplate.save(m1);
        mongoTemplate.save(org);

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, contractCreator)
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "pink man")
                .set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

    }

    @After
    public void tearDown() {
        mongoTemplate.remove(new Query(Criteria.where("tx_hash").is("hash1")), Organization.class);
        mongoTemplate.remove(new Query(Criteria.where("address").is(contractCreator)), Member.class);
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.eq(1L)).execute();
    }

    @Test
    public void listMember() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/member/list")
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
    public void checkChangeMember() throws Exception {
        String json1 = "{\n" +
                "  \"change_type\": 4,\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/member/change/check")
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
    public void changeMember() throws Exception {
        String json1 = "{\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"change_type\": 7,\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"target_address\":\"0x66b2cb4aeb52a4a291446e54aa4af0f0fec7983ec0\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/member/change")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"change_type\": 4,\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/member/change")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66b2cb4aeb52a4a291446e54aa4af0f0fec7983ec0")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("permission denied"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();


    }

    @Test
    public void checkConfirmMemberChange() throws Exception {
        String json1 = "{\n" +
                "  \"change_type\": 4,\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/member/confirm/change/check")
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
    public void confirmMemberChange() throws Exception {
        String json1 = "{\n" +
                "  \"call_data\": \"0xqqq\",\n" +
                "  \"change_type\": 7,\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/member/confirm/change")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"call_data\": \"0xqqq\",\n" +
                "  \"change_type\": 7,\n" +
                "  \"contract_address\":\"0x66b2cb4aeb52a4a291446e54aa4af0f0fec7983ec0\"," +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/member/confirm/change")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66b2cb4aeb52a4a291446e54aa4af0f0fec7983ec0")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2600))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}