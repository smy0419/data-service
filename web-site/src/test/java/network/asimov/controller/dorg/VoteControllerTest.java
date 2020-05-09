package network.asimov.controller.dorg;

import network.asimov.mongodb.entity.dorg.Member;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.entity.dorg.Proposal;
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
 * @date 2020-04-01
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class VoteControllerTest {
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

        dSLContext.insertInto(Tables.T_DAO_MESSAGE)
                .set(Tables.T_DAO_MESSAGE.ID, 1L)
                .set(Tables.T_DAO_MESSAGE.CATEGORY, 1)
                .set(Tables.T_DAO_MESSAGE.TYPE, 1)
                .set(Tables.T_DAO_MESSAGE.MESSAGE_POSITION, 0)
                .set(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_MESSAGE.RECEIVER, contractCreator)
                .set(Tables.T_DAO_MESSAGE.ADDITIONAL_INFO, "{\"proposal_id\":99,\"decision\":true}")
                .set(Tables.T_DAO_MESSAGE.STATE, 0)
                .set(Tables.T_DAO_MESSAGE.CREATE_TIME, now)
                .set(Tables.T_DAO_MESSAGE.UPDATE_TIME, now)
                .execute();

        Proposal p = new Proposal();
        p.setContractAddress(contractAddress);
        p.setStatus(0);
        p.setEndTime(TimeUtil.currentSeconds() + TimeUtil.SECONDS_OF_DAY * 7);
        p.setProposalId(99L);
        p.setTxHash("hash1");
        p.setAddress(contractCreator);

        Organization o1 = new Organization();
        o1.setOrgName("org_name");
        o1.setOrgId(1);
        o1.setContractAddress(contractAddress);
        o1.setPresident(contractCreator);
        o1.setStatus(0);
        o1.setTime(now);

        Member m1 = new Member();
        m1.setContractAddress(contractAddress);
        m1.setAddress(contractCreator);
        m1.setRole(0);
        m1.setStatus(1);

        mongoTemplate.save(p);
        mongoTemplate.save(o1);
        mongoTemplate.save(m1);
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_MESSAGE).where(Tables.T_DAO_MESSAGE.ID.eq(1L)).execute();

        mongoTemplate.remove(new Query(Criteria.where("proposal_id").is(99)), Proposal.class);
        mongoTemplate.remove(new Query(Criteria.where("contract_address").is(contractAddress)), Organization.class);
        mongoTemplate.remove(new Query(Criteria.where("address").is(contractCreator)), Member.class);
    }

    @Test
    public void checkVote() throws Exception {
        String json1 = "{\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"decision\": false,\n" +
                "  \"proposal_id\": 99\n" +
                "}";
        mvc.perform(MockMvcRequestBuilders.post("/dao/org/vote/check")
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
    public void vote() throws Exception {
        String json1 = "{\n" +
                "  \"call_data\": \"0xqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"decision\": false,\n" +
                "  \"proposal_id\": 1\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/vote")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header("token", "0x6661f5d5638a5d1f4d88fa223c11526465a5dcb35d")
                .content(json1.getBytes())
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
                .andExpect(MockMvcResultMatchers.jsonPath("msg").value("permission denied"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String json2 = "{\n" +
                "  \"call_data\": \"0xqqq\",\n" +
                "  \"contract_address\":\"" + contractAddress + "\",\n" +
                "  \"decision\": false,\n" +
                "  \"proposal_id\": 99\n" +
                "}";

        mvc.perform(MockMvcRequestBuilders.post("/dao/org/vote")
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