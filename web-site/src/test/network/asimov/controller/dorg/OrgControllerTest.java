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
public class OrgControllerTest {
    long now = TimeUtil.currentSeconds();
    private String contractAddress = "0x6387a3b8a6b78b18d2984ef1f9c7acf8c39be0b515";

    private String contractCreator = "0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812";

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

        dSLContext.insertInto(Tables.T_DAO_ORGANIZATION)
                .set(Tables.T_DAO_ORGANIZATION.ID, 1L)
                .set(Tables.T_DAO_ORGANIZATION.TX_HASH, "hash111")
                .set(Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_ORGANIZATION.VOTE_CONTRACT_ADDRESS, "")
                .set(Tables.T_DAO_ORGANIZATION.CREATOR_ADDRESS, contractCreator)
                .set(Tables.T_DAO_ORGANIZATION.ORG_NAME, "org1")
                .set(Tables.T_DAO_ORGANIZATION.ORG_LOGO, "org_logo")
                .set(Tables.T_DAO_ORGANIZATION.STATE, (byte) 1)
                .set(Tables.T_DAO_ORGANIZATION.CREATE_TIME, now)
                .set(Tables.T_DAO_ORGANIZATION.UPDATE_TIME, now)
                .execute();

        Member m1 = new Member();
        m1.setStatus(1);
        m1.setRole(0);
        m1.setAddress(contractCreator);
        m1.setContractAddress(contractAddress);

        Organization o1 = new Organization();
        o1.setContractAddress(contractAddress);
        o1.setVoteContractAddress("");
        o1.setPresident(contractCreator);
        o1.setStatus(0);
        o1.setOrgId(1);
        o1.setOrgName("org1");
        o1.setHeight(999L);

        mongoTemplate.save(m1);
        mongoTemplate.save(o1);
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_ORGANIZATION).where(Tables.T_DAO_ORGANIZATION.ID.eq(1L)).execute();
        mongoTemplate.remove(new Query(Criteria.where("address").is(contractCreator)), Member.class);
        mongoTemplate.remove(new Query(Criteria.where("contract_address").is(contractAddress)), Organization.class);
    }


    @Test
    public void listOrg() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/list")
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
    public void createPrepare() throws Exception {
        String json1 = "{\n" +
                "  \"org_name\": \"suds\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/create/prepare")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data.data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void createCheck() throws Exception {
        String json1 = "{\n" +
                "  \"org_name\": \"suds\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/create/check")
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

        // miss token
        String json2 = "{\n" +
                "  \"org_name\": \"suds\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/create/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1006))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("missing token"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void createOrg() throws Exception {
        // org name repeat
        String json1 = "{\n" +
                "  \"org_name\": \"org1\",\n" +
                "\"call_data\":\"0xdsdsd\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2502))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("repeat organization name"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // miss token
        String json2 = "{\n" +
                "  \"org_name\": \"ddd\",\n" +
                "\"call_data\":\"0xdsdsd\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/create/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1006))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("missing token"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json3 = "{\n" +
                "  \"org_name\": \"ddd\",\n" +
                "  \"org_logo\": \"logo_url\",\n" +
                "\"call_data\":\"0xdsdsd\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void checkClose() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/close/check")
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

        // permission denied
        String json2 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/close/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6661f5d5638a5d1f4d88fa223c11526465a5dcb35d")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("permission denied"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // org not exists
        String json3 = "{\n" +
                "  \"contract_address\": \"0xdsdsdsdd\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/close/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void closeOrg() throws Exception {
        String json1 = "{\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/close")
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

        // permission denied
        String json2 = "{\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/close")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6661f5d5638a5d1f4d88fa223c11526465a5dcb35d")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("permission denied"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // org not exists
        String json3 = "{\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\": \"0xdsdsdsdd\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/close")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void checkUpdate() throws Exception {
        String json1 = "{\n" +
                "  \"org_name\": \"qqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/update/check")
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
                "  \"org_name\": \"org1\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/update/check")
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
    }

    @Test
    public void updateOrgName() throws Exception {
        String json1 = "{\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"org_name\": \"qqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/update/name")
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
                "  \"org_name\": \"org1\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/update/name")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2502))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("repeat organization name"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void updateOrgLogo() throws Exception {
        String json1 = "{\n" +
                "  \"org_logo\": \"qqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/update/logo")
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

        // permission denied
        String json2 = "{\n" +
                "  \"org_logo\": \"qqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/update/logo")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6661f5d5638a5d1f4d88fa223c11526465a5dcb35d")
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
    public void getOrgInfo() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        // org not exists
        String json2 = "{\n" +
                "  \"contract_address\":\"0x6366bb5547459ec4460ce2df96595e4069912d9901\"" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6661f5d5638a5d1f4d88fa223c11526465a5dcb35d")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}