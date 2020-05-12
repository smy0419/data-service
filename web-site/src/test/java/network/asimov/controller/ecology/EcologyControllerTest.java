package network.asimov.controller.ecology;

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
public class EcologyControllerTest {
	long now = TimeUtil.currentSeconds();

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mvc;

	@Autowired
	private DSLContext dSLContext;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();
		dSLContext.insertInto(Tables.T_CHAIN_NODE)
				.set(Tables.T_CHAIN_NODE.ID, 1L)
				.set(Tables.T_CHAIN_NODE.IP, "10.102.0.14")
				.set(Tables.T_CHAIN_NODE.CITY, "hangzhou")
				.set(Tables.T_CHAIN_NODE.SUBDIVISION, "qqq")
				.set(Tables.T_CHAIN_NODE.COUNTRY, "china")
				.set(Tables.T_CHAIN_NODE.LONGITUDE, "123.4444")
				.set(Tables.T_CHAIN_NODE.LATITUDE, "222.4444")
				.set(Tables.T_CHAIN_NODE.CREATE_TIME, now)
				.set(Tables.T_CHAIN_NODE.UPDATE_TIME, now)
				.execute();
	}

	@After
	public void tearDown() throws Exception {
		dSLContext.delete(Tables.T_CHAIN_NODE).where(Tables.T_CHAIN_NODE.ID.in(1L)).execute();
	}

	@Test
	public void listChainNode() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/ecology/node")
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