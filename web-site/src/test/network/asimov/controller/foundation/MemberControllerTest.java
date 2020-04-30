package network.asimov.controller.foundation;

import network.asimov.mongodb.entity.foundation.Member;
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
 * @date 2020-04-01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class MemberControllerTest {
    long now = TimeUtil.currentSeconds();

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DSLContext dSLContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        Member m = mongoTemplate.findOne(new Query(), Member.class);

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, m.getAddress())
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "pink man")
                .set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                .execute();

    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.eq(1L)).execute();
    }

    @Test
    public void listMember() throws Exception {
        String json1 = "{\n" +
                "  \"page\": {\n" +
                "    \"index\": 1,\n" +
                "    \"limit\": 10\n" +
                "  }\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/foundation/member/list")
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
    public void getMemberInfo() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/foundation/member/position")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("data").exists())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }
}