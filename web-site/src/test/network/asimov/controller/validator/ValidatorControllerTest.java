package network.asimov.controller.validator;

import network.asimov.mongodb.entity.validator.BtcMiner;
import network.asimov.mongodb.entity.validator.Earning;
import network.asimov.mongodb.entity.validator.EarningAsset;
import network.asimov.mongodb.entity.validator.ValidatorRelation;
import network.asimov.util.TimeUtil;
import org.bson.types.ObjectId;
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
 * @date 2020-04-03
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class ValidatorControllerTest {
    long now = TimeUtil.currentSeconds();

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    protected DSLContext dSLContext;

    private MockMvc mvc;

    ObjectId id1 = ObjectId.get();
    ObjectId id2 = ObjectId.get();

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();


        Earning earning1 = new Earning();
        earning1.setId(id1);
        earning1.setAddress("addr1");
        earning1.setBtcMinerAddress("btc_addr1");
        earning1.setTxHash("hash");

        Earning earning2 = new Earning();
        earning2.setId(id2);
        earning2.setAddress("addr1");
        earning2.setBtcMinerAddress("btc_addr1");
        earning2.setTxHash("hash");

        EarningAsset e1 = new EarningAsset();
        e1.setEarningId(id1);
        e1.setTime(now - 10000);
        e1.setAsset("000000000000000000000000");
        e1.setValue(12345L);

        EarningAsset e2 = new EarningAsset();
        e2.setEarningId(id2);
        e2.setTime(now - 20000);
        e2.setAsset("000000000000000000000000");
        e2.setValue(67890L);

        BtcMiner b1 = new BtcMiner();
        b1.setAddress("btc_addr1");
        b1.setDomain("domain1");

        BtcMiner b2 = new BtcMiner();
        b2.setAddress("btc_addr2");
        b2.setDomain("domain2");

        ValidatorRelation v1 = new ValidatorRelation();
        v1.setBind(true);
        v1.setBtcMinerAddress("btc_addr1");
        v1.setAddress("addr1");

        mongoTemplate.save(earning1);
        mongoTemplate.save(earning2);
        mongoTemplate.save(e1);
        mongoTemplate.save(e2);
        mongoTemplate.save(b1);
        mongoTemplate.save(b2);
        mongoTemplate.save(v1);
    }

    @After
    public void tearDown() {
        mongoTemplate.remove(new Query(Criteria.where("id").is(id1)), Earning.class);
        mongoTemplate.remove(new Query(Criteria.where("id").is(id1)), Earning.class);
        mongoTemplate.remove(new Query(Criteria.where("earning_id").is(id1)), EarningAsset.class);
        mongoTemplate.remove(new Query(Criteria.where("earning_id").is(id2)), EarningAsset.class);
        mongoTemplate.remove(new Query(Criteria.where("address").is("btc_addr1")), BtcMiner.class);
        mongoTemplate.remove(new Query(Criteria.where("address").is("btc_addr2")), BtcMiner.class);
        mongoTemplate.remove(new Query(Criteria.where("address").is("addr1")), ValidatorRelation.class);
    }

    @Test
    public void getValidatorRole() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/role")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6632032786c61472128d1b3185c92626f8ff0ee4d3")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/role")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void getValidatorNumber() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/number")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void getMyEarning() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/earning/mine")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "addr1")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }

    @Test
    public void listValidator() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/list")
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
    public void getValidatorLocation() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/location")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listRewardDetail() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/reward/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6632032786c61472128d1b3185c92626f8ff0ee4d3")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/reward/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user111")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void balance() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6632032786c61472128d1b3185c92626f8ff0ee4d3")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66c85ccae8d10cfc7b96d410c82675c3ca10776df0")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void listEarning() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/earning/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6632032786c61472128d1b3185c92626f8ff0ee4d3")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/earning/list")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "user111")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void getTotalEarning() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/validation/validator/earning/total")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}