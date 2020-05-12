package network.asimov.controller.foundation;

import network.asimov.mongodb.entity.foundation.Member;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.entity.foundation.TodoList;
import network.asimov.mongodb.entity.foundation.Vote;
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
public class ProposalControllerTest {
	@Autowired
	protected DSLContext dSLContext;
	long now = TimeUtil.currentSeconds();
	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private MongoTemplate mongoTemplate;

	private MockMvc mvc;

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

		dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
				.set(Tables.T_DAO_ACCOUNT.ID, 2L)
				.set(Tables.T_DAO_ACCOUNT.ADDRESS, "user2")
				.set(Tables.T_DAO_ACCOUNT.NICK_NAME, "test user 2")
				.set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
				.set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
				.set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
				.execute();

		dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
				.set(Tables.T_DAO_ACCOUNT.ID, 3L)
				.set(Tables.T_DAO_ACCOUNT.ADDRESS, "user3")
				.set(Tables.T_DAO_ACCOUNT.NICK_NAME, "test user 3")
				.set(Tables.T_DAO_ACCOUNT.AVATAR, "http://sdsdsd")
				.set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
				.set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
				.execute();

		// insert proposal operation
		dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
				.set(Tables.T_FOUNDATION_OPERATION.ID, 1L)
				.set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash111")
				.set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 1)
				.set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{}")
				.set(Tables.T_FOUNDATION_OPERATION.OPERATOR, m.getAddress())
				.set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
				.set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
				.execute();

		dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
				.set(Tables.T_FOUNDATION_OPERATION.ID, 2L)
				.set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash222")
				.set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 1)
				.set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{}")
				.set(Tables.T_FOUNDATION_OPERATION.OPERATOR, m.getAddress())
				.set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
				.set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
				.execute();


		Proposal p = new Proposal();
		p.setStatus(0);
		p.setEndTime(now - TimeUtil.SECONDS_OF_DAY * 7);
		p.setProposalId(1L);
		p.setTxHash("hash111");
		p.setAddress(m.getAddress());
		p.setTime(now);
		p.setProposalType(0);


		TodoList todo1 = new TodoList();
		todo1.setTodoId(1L);
		todo1.setOperator(m.getAddress());
		todo1.setOperated(true);

		TodoList todo2 = new TodoList();
		todo2.setTodoId(1L);
		todo2.setOperator("user2");
		todo2.setOperated(true);

		TodoList todo3 = new TodoList();
		todo3.setTodoId(1L);
		todo3.setOperator("user3");
		todo3.setOperated(false);

		Vote v1 = new Vote();
		v1.setDecision(true);
		v1.setProposalId(1L);
		v1.setTxHash("hash333");
		v1.setVoter(m.getAddress());
		v1.setTime(now);

		Vote v2 = new Vote();
		v2.setDecision(false);
		v2.setProposalId(1L);
		v2.setTxHash("hash4444");
		v2.setVoter("user2");
		v2.setTime(now);

		mongoTemplate.save(p);
		mongoTemplate.save(todo1);
		mongoTemplate.save(todo2);
		mongoTemplate.save(todo3);
		mongoTemplate.save(v1);
		mongoTemplate.save(v2);
	}

	@After
	public void tearDown() {
		dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.in(1L, 2L, 3L)).execute();
		dSLContext.delete(Tables.T_FOUNDATION_OPERATION).where(Tables.T_FOUNDATION_OPERATION.ID.in(1L, 2L)).execute();
		mongoTemplate.remove(new Query(Criteria.where("proposal_id").is(1L)), Proposal.class);
		mongoTemplate.remove(new Query(Criteria.where("todo_id").is(1L)), TodoList.class);
		mongoTemplate.remove(new Query(Criteria.where("proposal_id").is(1L)), Vote.class);
	}

	@Test
	public void listMyProposal() throws Exception {
		Member m = mongoTemplate.findOne(new Query(), Member.class);
		String json1 = "{\n" +
				"  \"page\": {\n" +
				"    \"index\": 1,\n" +
				"    \"limit\": 10\n" +
				"  }\n" +
				"}";

		mvc.perform(MockMvcRequestBuilders.post("/foundation/proposal/mine")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.header("token", m.getAddress())
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
	public void listHistoryProposal() throws Exception {
		String json1 = "{\n" +
				"  \"page\": {\n" +
				"    \"index\": 1,\n" +
				"    \"limit\": 10\n" +
				"  }\n" +
				"}";
		mvc.perform(MockMvcRequestBuilders.post("/foundation/proposal/history")
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
	public void getProposalById() throws Exception {
		String json1 = "{\n" +
				"  \"id\": 1\n" +
				"}";
		mvc.perform(MockMvcRequestBuilders.post("/foundation/proposal/info")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.header("token", "user1")
				.content(json1.getBytes())
		)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json2 = "{\n" +
				"  \"id\": -1\n" +
				"}";
		mvc.perform(MockMvcRequestBuilders.post("/foundation/proposal/info")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.header("token", "user1")
				.content(json2.getBytes())
		)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void checkLaunch() throws Exception {
		String json1 = "{\n" +
				"  \"invest_amount\": 1000000000,\n" +
				"  \"proposal_type\": 2,\n" +
				"  \"target_address\": \"0x664d34218237d4958a7bf6b60151f2092f50fa81ee\"\n" +
				"}";

		mvc.perform(MockMvcRequestBuilders.post("/foundation/proposal/launch/check")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.header("token", "0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")
				.content(json1.getBytes())
		)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

	}

	@Test
	public void launch() throws Exception {
		String json1 = "{\n" +
				"  \"call_data\": \"0x12123\",\n" +
				"  \"proposal_detail\": {\n" +
				"    \"comment\": \"这是提议说明...\",\n" +
				"    \"invest_amount\": \"999999\",\n" +
				"    \"invest_asset\": \"000000000000000000000000\",\n" +
				"    \"proposal_type\": \"2\",\n" +
				"    \"target_address\": \"0x660ed67abdd8ff954fdc94035d8fa339953cee7c15\"\n" +
				"  }\n" +
				"}";
		mvc.perform(MockMvcRequestBuilders.post("/foundation/proposal/launch")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.header("token", "0x66b28f9dd1cf6314a8b5d691aeec6c6eaf456cbd9a")
				.content(json1.getBytes())
		)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(2300))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		mvc.perform(MockMvcRequestBuilders.post("/foundation/proposal/launch")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.header("token", "user1")
				.content(json1.getBytes())
		)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1008))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();


	}
}