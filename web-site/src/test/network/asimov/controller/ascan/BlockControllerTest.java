package network.asimov.controller.ascan;

import network.asimov.mongodb.entity.ascan.Block;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
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
 * @date 2020-03-16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class BlockControllerTest {
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
    public void queryBlock() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/query")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\"time\":0}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/query")
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
    }

    @Test
    public void getHandledBlockHeight() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/height")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void blockDetail1() throws Exception {
        Block block = mongoTemplate.findOne(new Query(), Block.class);
        String json1 = "{\"hash\": \"" + block.getHash() + "\"}";

        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"height\": 11\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json2.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json3 = "{}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json3.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1002))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("invalid arguments : height or hash not null"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json4 = "{\n" +
                "  \"height\": -1\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json4.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json5 = "{\n" +
                "  \"height\": 999999999\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json5.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json6 = "{\"hash\": \"8492587c11a3d379f1adf068b103ba8e8313e2d7342f48ab3bc62c4d5a571475\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json6.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void blockDetail2() throws Exception {
        Block t = mongoTemplate.findOne(new Query(), Block.class);
        String json7 = "{\"hash\": \"" + t.getHash() + "\"}";
        mvc.perform(MockMvcRequestBuilders.post("/ascan/block/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json7.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

}