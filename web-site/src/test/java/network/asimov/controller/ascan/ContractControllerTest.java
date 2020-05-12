package network.asimov.controller.ascan;

import network.asimov.mongodb.entity.ascan.ContractTransaction;
import network.asimov.mongodb.entity.ascan.TransactionCount;
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
 * @date 2020-03-16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class ContractControllerTest {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private MongoTemplate mongoTemplate;

	private MockMvc mvc;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void getContractDetail() throws Exception {
		String json1 = "{}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/detail")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json1.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : address not blank"))
				.andDo(MockMvcResultHandlers.print())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json2 = "{\"address\":\"0x111111111111\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/detail")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json2.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		TransactionCount t1 =
				mongoTemplate.findOne(new Query(Criteria.where("category").is(2)), TransactionCount.class);
		String json3 = "{\"address\":\"" + t1.getKey() + "\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/detail")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json3.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void listContractTransaction() throws Exception {
		String json1 = "{\"address\":\"0x66e3054b411051da5492aec7a823b00cb3add772d7\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/transaction/query")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json1.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : page required"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json2 = "{\"page\": {\"index\": 1,\"limit\": 10}}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/transaction/query")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json2.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : address not blank"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json3 =
				"{"
						+ " \"address\":\"0x630000000000000000000000000000000000000061\","
						+ "  \"page\": {\n"
						+ "    \"index\": 1,\n"
						+ "    \"limit\": 10\n"
						+ "  }\n"
						+ "}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/transaction/query")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json3.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("data.total").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		TransactionCount t1 =
				mongoTemplate.findOne(new Query(Criteria.where("category").is(2)), TransactionCount.class);
		String json4 = "{"
				+ " \"address\":\""
				+ t1.getKey()
				+ "\","
				+ "  \"page\": {\n"
				+ "    \"index\": 1,\n"
				+ "    \"limit\": 10\n"
				+ "  }\n"
				+ "}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/transaction/query")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json4.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void listContract() throws Exception {
		String json1 = "{" + "  \"page\": {\n" + "    \"index\": 1,\n" + "    \"limit\": 10\n" + "  }\n" + "}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/query")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json1.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("data.total").exists())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void getContractSource() throws Exception {
		String json1 = "{\"name\":\"qqq\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/info")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json1.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : category not null"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		String json2 = "{\"category\":1}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/info")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json2.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : name not blank"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json3 = "{\"category\":11,\"name\":\"dao\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/info")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json3.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json4 = "{\"category\":1,\"name\":\"daodd\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/info")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json4.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json5 = "{\"category\":1,\"name\":\"dao\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/info")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json5.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("data.abi").exists())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}

	@Test
	public void listBalances() throws Exception {
		String json1 = "{}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/balances")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json1.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1002))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("invalid arguments : address not blank"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		String json2 = "{\"address\":\"0x12344545\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/balances")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json2.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(1009))
				.andExpect(MockMvcResultMatchers.jsonPath("msg").value("data not exists"))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();

		ContractTransaction t1 = mongoTemplate.findOne(new Query(), ContractTransaction.class);
		String json3 = "{\"address\":\"" + t1.getKey() + "\"}";
		mvc.perform(
				MockMvcRequestBuilders.post("/ascan/contract/balances")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.content(json3.getBytes()))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(MockMvcResultMatchers.jsonPath("code").value(0))
				.andExpect(MockMvcResultMatchers.jsonPath("data.balances").exists())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
	}
}
