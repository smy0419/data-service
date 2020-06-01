package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.TransactionCount;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-05-09
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TransactionStatisticsServiceTest extends TransactionStatisticsService {
	@Before
	public void setUp() throws Exception {
		TransactionCount t1 = new TransactionCount();
		t1.setKey("0x666");
		t1.setTxCount(111L);
		t1.setCategory(1);
		mongoTemplate.save(t1);

		TransactionCount t2 = new TransactionCount();
		t2.setKey("0x999");
		t2.setTxCount(222L);
		t2.setCategory(2);
		mongoTemplate.save(t2);

		TransactionCount t3 = new TransactionCount();
		t3.setKey("0x888");
		t3.setTxCount(333L);
		t3.setCategory(3);
		mongoTemplate.save(t3);

//		AddressTransaction a1 = new AddressTransaction();
//		a1.setKey("0x666");
//		a1.setTxHash("hash1");
//		a1.setHeight(1L);
//		mongoTemplate.save(a1);
//
//		AddressTransaction a2 = new AddressTransaction();
//		a2.setKey("0x666");
//		a2.setTxHash("hash2");
//		a2.setHeight(2L);
//		mongoTemplate.save(a2);
	}

	@After
	public void tearDown() throws Exception {
		Query query = new Query(Criteria.where("key").in("0x666", "0x999", "0x888"));
		mongoTemplate.remove(query, TransactionCount.class);

//		mongoTemplate.remove(new Query(Criteria.where("key").is("0x666")), AddressTransaction.class);
	}

	@Test
	public void testGet() {
		Optional<TransactionCount> p1 = get(null);
		Assert.assertTrue(!p1.isPresent());

		Optional<TransactionCount> p2 = get("0x666");
		Assert.assertTrue(p2.isPresent());
	}

	@Test
	public void testCount() {
		long count1 = count(null);
		Assert.assertTrue(count1 == 0);

		long count2 = count("sdsdsd");
		Assert.assertTrue(count2 == 0);

		long count3 = count("0x888");
		Assert.assertTrue(count3 == 333);
	}

	@Test
	public void testQueryDataByPage() {
		Pair<Long, List<TransactionCount>> p1 = queryDataByPage(TransactionCount.TxCountCategory.Address, 1, 10);
		Assert.assertTrue(p1.getRight().size() >= 1);
	}

//	@Test
//	public void testQueryTxListByPage() {
//		Pair<Long, List<AddressTransaction>> p1 = queryTxListByPage(AddressTransaction.class, "0x666", 1, 10);
//		Assert.assertTrue(p1.getRight().size() == 2);
//	}
}
