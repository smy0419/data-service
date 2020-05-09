package network.asimov.controller.ascan;

import network.asimov.mongodb.entity.ascan.AddressAssetBalance;
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
public class AddressControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc mvc;

    @Before
    public void setupMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getAddressDetail() throws Exception {
        String json1 = "{\"address\":\"0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89cfff\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/detail")
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

        AddressAssetBalance aab = mongoTemplate.findOne(new Query(Criteria.where("asset").is("000000000000000000000000").and("balance").gt(0)), AddressAssetBalance.class);
        String json3 = "{\"address\":\"" + aab.getAddress() + "\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("0"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listAddressTransaction() throws Exception {
        String json1 = "{\n" +
                "  \"address\": \"0x66e3054b411051da5492aec7a823b00cb3add772d7\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/transaction/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("data.total").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/transaction/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : address not blank"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json3 = "{\"address\":\"0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812\"}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/transaction/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : page required"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json4 = "{\n" +
                "  \"address\": \"0x66e3054b411051da5492aec7a823b00cb3add772d7\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 0,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/transaction/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json4.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : page.index greater than or equal to 1"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json5 = "{\n" +
                "  \"address\": \"0x66e3054b411051da5492aec7a823b00cb3add772d7\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 0\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/transaction/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json5.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : page.limit greater than or equal to 1"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json6 = "{\n" +
                "  \"address\": \"0x66e3054b411051da5492aec7a823b00cb3add772d7\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 101\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/transaction/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json6.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : page.limit less than or equal to 100"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json7 = "{\n" +
                "  \"address\": \"0x6632032786c61472128d1b3185c92626f8ff0ee4d3\",\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/transaction/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json7.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listBalances() throws Exception {
        String json1 = "{\"address\":\"0x6662e907b15cbf27d5425399ebf6f0fb50ebb88fff\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/balances")
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

        String json2 = "{\"address\":\"0x66c0309e\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/balances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        AddressAssetBalance aab = mongoTemplate.findOne(new Query(Criteria.where("balance").gt(0)), AddressAssetBalance.class);
        String json4 = "{\"address\":\"" + aab.getAddress() + "\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/address/balances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json4.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }
}