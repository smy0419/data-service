package network.asimov.mongodb.service.ascan;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.ascan.Transaction;
import network.asimov.mongodb.entity.ascan.Vin;
import network.asimov.mongodb.entity.ascan.Vout;
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
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TransactionServiceTest extends TransactionService {

    @Before
    public void setUp() throws Exception {
        List<String> list1 = Lists.newArrayList();
        list1.add("000000010000000000000001");
        list1.add("000000010000000000000002");

        Transaction t1 = new Transaction();
        t1.setHeight(1L);
        t1.setHash("hash1");
//        t1.setAssets(list1);
        t1.setTime(System.currentTimeMillis() / 1000 + 10);

        Vin v1 = new Vin();
        v1.setPrevOut(new Vin.PrevOut());


        List<String> list2 = Lists.newArrayList();
        list2.add("000000010000000000000001");
        Transaction t2 = new Transaction();
        t2.setHeight(2L);
        t2.setHash("hash2");
//        t2.setAssets(list2);
        t2.setTime(System.currentTimeMillis() / 1000 + 10);

        Transaction t3 = new Transaction();
        t3.setHeight(3L);
        t3.setHash("hash3");

//        AssetTransaction at1 = new AssetTransaction();
//        at1.setAsset("000000010000000000000001");
//        at1.setTxCount(1L);
//
//        AssetTransaction at2 = new AssetTransaction();
//        at2.setAsset("000000010000000000000002");
//        at2.setTxCount(1L);

//        mongoTemplate.save(at1);
//        mongoTemplate.save(at2);

        mongoTemplate.save(t1);
        mongoTemplate.save(t2);
        mongoTemplate.save(t3);


    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("hash").in("hash1", "hash2", "hash3"));
        mongoTemplate.remove(query, Transaction.class);

        Query query2 = new Query(Criteria.where("hash").in("hash4", "hash5"));
        mongoTemplate.remove(query2, Vin.class);

        Query query3 = new Query(Criteria.where("hash").in("hash6", "hash7"));
        mongoTemplate.remove(query3, Vout.class);

//        Query query4 = new Query(Criteria.where("asset").in("000000010000000000000001", "000000010000000000000002"));
//        mongoTemplate.remove(query4, AssetTransaction.class);

    }

    @Test
    public void testListTransaction() {
        Pair<Long, List<Transaction>> pair = listTransaction(1L, 1, 10);
        Assert.assertTrue(pair.getRight().size() > 0);
    }

//    @Test
//    public void testQueryBlockByPage() {
//        Integer index = 1;
//        Integer limit = 10;
//
//        Pair<Long, List<Transaction>> p1 = queryBlockByPage(index, limit);
//        Assert.assertTrue(p1.getRight().size() > 0);
//    }

    @Test
    public void testGetByHash() {
        Optional<Transaction> op1 = get("hash3");
        Assert.assertTrue(op1.isPresent());

        Optional<Transaction> op2 = get("hash33");
        Assert.assertTrue(!op2.isPresent());
    }

    @Test
    public void testCount() {
        long count = count();
        Assert.assertTrue(count > 0);
    }

    @Test
    public void testCountByAsset() {
//        String asset1 = "000000010000000000000001";
//        long count1 = countByAsset(asset1);
//        Assert.assertEquals(1, count1);
//
//        String asset2 = "000000010000000000000002";
//        long count2 = countByAsset(asset2);
//        Assert.assertEquals(1, count2);
//
//        String asset3 = "000000010000000000000003";
//        long count3 = countByAsset(asset3);
//        Assert.assertEquals(0, count3);
    }

    @Test
    public void testQueryTransactionByAsset() {
//        Integer index = 1;
//        Integer limit = 10;
//        String asset1 = "000000010000000000000001";
//        Pair<Long, List<Transaction>> p1 = queryTransactionByAsset(index, limit, asset1);
//        Assert.assertEquals(1, p1.getRight().size());
//
//        String asset2 = "000000010000000000000002";
//        Pair<Long, List<Transaction>> p2 = queryTransactionByAsset(index, limit, asset2);
//        Assert.assertEquals(1, p2.getRight().size());
    }

}