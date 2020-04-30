package network.asimov.controller.foundation;

import network.asimov.mysql.database.Tables;
import network.asimov.util.TimeUtil;
import org.jooq.DSLContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class OperationControllerTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    protected DSLContext dSLContext;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 1L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash111")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 1)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "user1")
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, TimeUtil.currentSeconds())
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, TimeUtil.currentSeconds())
                .execute();
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_FOUNDATION_OPERATION).where(Tables.T_FOUNDATION_OPERATION.ID.eq(1L)).execute();
    }

    @Test
    public void getTxStatusByHash() throws Exception {
        String json1 = "{\n" +
                "  \"tx_hash\": \"96c4cb212254c59d15ae5b333cdbd515324090e67dd468c75dd8ab9cf9188480\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/foundation/transaction/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"tx_hash\": \"hash111\"\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/foundation/transaction/status")
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