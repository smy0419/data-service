package network.asimov.controller.ascan;

import network.asimov.mongodb.entity.ascan.AssetIssue;
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
 * @date 2020-03-13
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class AssetControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setupMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        AssetIssue a1 = new AssetIssue();
        a1.setValue(111L);
        a1.setAsset("000000000000028a00000000");
        a1.setIssueType(0);

        AssetIssue a2 = new AssetIssue();
        a2.setValue(222L);
        a2.setAsset("000000000000028a00000000");
        a2.setIssueType(1);

        mongoTemplate.save(a1);
        mongoTemplate.save(a2);
    }

    @After
    public void tearDown() {
        mongoTemplate.remove(new Query(Criteria.where("asset").is("000000000000028a00000000")), AssetIssue.class);
    }

    @Test
    public void getAssetDetailInfo() throws Exception {
        String json1 = "{\"asset\":\"000000000000000000000001\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\"asset\":\"000000000000000000000000\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listTransactionByAsset() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/transaction/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : asset not blank"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\"asset\":\"000000000000000200000001\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/transaction/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : page required"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json4 = "{\n" +
                "  \"asset\": \"000000000000000000000000\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/transaction/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json4.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("data.total").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }

    @Test
    public void getAssetSummary() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{" +
                " \"time\":" + (System.currentTimeMillis() + 1000000) / 1000 + "," +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        String json3 = "{" +
                " \"time\":-1," +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json4 = "{" +
                " \"time\":" + System.currentTimeMillis() / 1000 + "," +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json4.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listIssueByAsset() throws Exception {
        String json1 = "{" +
                " \"asset\":\"110000010000100000000001\"," +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/issue/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        AssetIssue issueAsset = mongoTemplate.findOne(new Query(), AssetIssue.class);

        String json2 = "{" +
                " \"asset\":\"" + issueAsset.getAsset() + "\"," +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/asset/issue/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}