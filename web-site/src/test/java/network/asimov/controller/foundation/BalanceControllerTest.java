package network.asimov.controller.foundation;

import network.asimov.mongodb.entity.foundation.BalanceSheet;
import network.asimov.util.TimeUtil;
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
public class BalanceControllerTest {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private MongoTemplate mongoTemplate;

	private MockMvc mvc;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();
		BalanceSheet b1 = new BalanceSheet();
		b1.setAddress("addr1");
		b1.setAsset("000000000000000000000000");
		b1.setAmount(8888L);
		b1.setTxHash("hash1");
		b1.setProposalType(1);
		b1.setTransferType(1);
		b1.setTime(TimeUtil.currentSeconds());
		b1.setHeight(111L);

		BalanceSheet b2 = new BalanceSheet();
		b2.setAddress("addr2");
		b2.setAsset("000000000000000000000000");
		b2.setAmount(6666L);
		b2.setTxHash("hash2");
		b2.setProposalType(0);
		b2.setTransferType(1);
		b2.setTime(TimeUtil.currentSeconds());
		b2.setHeight(123L);

		mongoTemplate.save(b1);
		mongoTemplate.save(b2);

	}

	@After
	public void tearDown() {
		mongoTemplate.remove(new Query(Criteria.where("hash").in("hash1", "hash2")), BalanceSheet.class);
	}

	@Test
	public void balance() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/foundation/balance")
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
	public void balanceSheet() throws Exception {
		String json1 = "{\n" +
				"  \"page\": {\n" +
				"    \"index\": 1,\n" +
				"    \"limit\": 10\n" +
				"  }\n" +
				"}";
		mvc.perform(MockMvcRequestBuilders.post("/foundation/balancesheet")
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
}