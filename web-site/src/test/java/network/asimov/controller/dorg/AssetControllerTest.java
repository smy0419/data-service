package network.asimov.controller.dorg;

import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.entity.dorg.OrganizationAsset;
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
 * @date 2020-03-27
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class AssetControllerTest {
    private String contractAddress = "0x6387a3b8a6b78b18d2984ef1f9c7acf8c39be0b515";

    private String contractCreator = "0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812";

    private String daoAsset = "000000000000028a00000000";

    long now = TimeUtil.currentSeconds();

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

        dSLContext.insertInto(Tables.T_DAO_ASSET)
                .set(Tables.T_DAO_ASSET.ID, 1L)
                .set(Tables.T_DAO_ASSET.TX_HASH, "hash1")
                .set(Tables.T_DAO_ASSET.ASSET, daoAsset)
                .set(Tables.T_DAO_ASSET.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_ASSET.DESCRIPTION, "desc")
                .set(Tables.T_DAO_ASSET.NAME, "asset_name")
                .set(Tables.T_DAO_ASSET.LOGO, "asset_logo")
                .set(Tables.T_DAO_ASSET.SYMBOL, "asset_symbol")
                .set(Tables.T_DAO_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_ASSET.UPDATE_TIME, now)
                .execute();

        Organization org = new Organization();
        org.setContractAddress(contractAddress);
        org.setPresident(contractCreator);
        org.setStatus(0);
        org.setOrgName("org1");
        org.setOrgId(1);
        org.setTxHash("hash1");

        OrganizationAsset oa = new OrganizationAsset();
        oa.setContractAddress(contractAddress);
        oa.setAsset(daoAsset);
        oa.setAssetIndex(1);
        oa.setAssetType(0);

        Asset asset = new Asset();
        asset.setHeight(10l);
        asset.setTime(999l);
        asset.setAsset(daoAsset);
        asset.setIssueAddress("0x11bbccsdds");

        mongoTemplate.save(asset);

        mongoTemplate.save(org);
        mongoTemplate.save(oa);
    }

    @After
    public void tearDown() {
        mongoTemplate.remove(new Query(Criteria.where("tx_hash").is("hash1")), Organization.class);
        mongoTemplate.remove(new Query(Criteria.where("asset").is(daoAsset)), OrganizationAsset.class);
        mongoTemplate.remove(new Query(Criteria.where("asset").is(daoAsset)), Asset.class);
        dSLContext.delete(Tables.T_DAO_ASSET).where(Tables.T_DAO_ASSET.ID.eq(1L)).execute();
    }

    @Test
    public void checkIssue() throws Exception {
        String json1 = "{\n" +
                "  \"asset_description\": \"qqq\",\n" +
                "  \"asset_logo\": \"fff\",\n" +
                "  \"asset_name\": \"sdsd\",\n" +
                "  \"asset_number\": \"123\",\n" +
                "  \"asset_symbol\": \"symbol\",\n" +
                "  \"contract_address\": \"0x630000000000000000000000000000000000000064\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/issue/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2600))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("organization not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void prepare() throws Exception {
        String json1 = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"asset_name\": \"qqq\",\n" +
                "  \"asset_symbol\": \"qqq\",\n" +
                "  \"asset_type\": 0,\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/issue/prepare")
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
    public void issueAsset() throws Exception {
        String json1 = "{\n" +
                "  \"amount_or_voucher_id\": 1000000,\n" +
                "  \"asset_desc\": \"qqq\",\n" +
                "  \"asset_name\": \"qqq\",\n" +
                "  \"asset_symbol\": \"qqq\",\n" +
                "  \"asset_type\": 0,\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"logo\": \"string\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/issue")
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
                "  \"amount_or_voucher_id\": 1,\n" +
                "  \"asset_desc\": \"qqq\",\n" +
                "  \"asset_name\": \"qqq\",\n" +
                "  \"asset_symbol\": \"qqq\",\n" +
                "  \"asset_type\": 1,\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"logo\": \"string\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json3 = "{\n" +
                "  \"amount_or_voucher_id\": 1,\n" +
                "  \"asset_desc\": \"qqq\",\n" +
                "  \"asset_name\": \"qqq\",\n" +
                "  \"asset_symbol\": \"qqq\",\n" +
                "  \"asset_type\": 1,\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"logo\": \"string\"\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66b2cb4aeb52a4a291446e54aa4af0f0fec7983ec0")
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void checkExpense() throws Exception {
        String json1 = "{\n" +
                "  \"amount\": 1111,\n" +
                "  \"asset\": \"000000000000000000000000\",\n" +
                "  \"asset_type\": 0,\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/expense/check")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2414))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }

    @Test
    public void expenseAsset() throws Exception {
        String json1 = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"asset\":\"000000000000000100000000\",\n" +
                "  \"asset_type\": 0,\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"," +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2414))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"amount\": 1,\n" +
                "  \"asset\":\"" + daoAsset + "\",\n" +
                "  \"asset_type\": 0,\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"target_address\":\"" + contractCreator + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/expense")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66b2cb4aeb52a4a291446e54aa4af0f0fec7983ec0")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listAsset() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/list")
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
    public void listIssueAsset() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\"" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/issue/list")
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
    public void checkMintAsset() throws Exception {
        String json1 = "{\n" +
                "  \"amount_or_voucher_id\": 1000000,\n" +
                "  \"asset\":\"" + daoAsset + "\",\n" +
                "  \"asset_type\": 0,\n" +
                "  \"contract_address\":\"" + contractAddress + "\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/mint/check")
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
    public void mintAsset1() throws Exception {
        String json1 = "{\n" +
                "  \"amount_or_voucher_id\": 1000000,\n" +
                "  \"asset\":\"" + daoAsset + "\",\n" +
                "  \"asset_type\": 0,\n" +
                "  \"asset_desc\": \"asset_desc\",\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/mint")
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


    }

    @Test
    public void mintAsset2() throws Exception {
        String json2 = "{\n" +
                "  \"amount_or_voucher_id\": 1000000,\n" +
                "  \"asset\":\"" + daoAsset + "\",\n" +
                "  \"asset_type\": 1,\n" +
                "  \"asset_desc\": \"asset_desc\",\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/mint")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66b2cb4aeb52a4a291446e54aa4af0f0fec7983ec0")
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void mintAsset3() throws Exception {
        String json2 = "{\n" +
                "  \"amount_or_voucher_id\": 1000000,\n" +
                "  \"asset\":\"" + daoAsset + "\",\n" +
                "  \"asset_type\": 1,\n" +
                "  \"asset_desc\": \"asset_desc\",\n" +
                "  \"call_data\": \"0xqqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/asset/mint")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", contractCreator)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}