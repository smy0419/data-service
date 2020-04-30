package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.Trading;
import network.asimov.util.TimeUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author sunmengyuan
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class TradingServiceTest extends TradingService {

    @Before
    public void setUp() throws Exception {
        Trading t = new Trading();
        t.setAsset("000000010000000000000001");
        t.setTime(System.currentTimeMillis() / 1000);

        Trading t2 = new Trading();
        t2.setAsset("000000010000000000000001");
        t2.setTime(System.currentTimeMillis() / 1000);

        Trading t3 = new Trading();
        t3.setAsset("000000010000000000000001");
        t3.setTime(System.currentTimeMillis() / 1000 - 2 * TimeUtil.SECONDS_OF_DAY);

        Trading t4 = new Trading();
        t4.setAsset("000000000000000000000002");
        t4.setValue(111L);
        t4.setTime(System.currentTimeMillis() / 1000);

        Trading t5 = new Trading();
        t5.setAsset("000000000000000000000002");
        t5.setValue(222L);
        t5.setTime(System.currentTimeMillis() / 1000);

        Trading t6 = new Trading();
        t6.setAsset("000000000000000000000002");
        t6.setValue(333L);
        t6.setTime(System.currentTimeMillis() / 1000 - 2 * TimeUtil.SECONDS_OF_DAY);

        mongoTemplate.save(t);
        mongoTemplate.save(t2);
        mongoTemplate.save(t3);
        mongoTemplate.save(t4);
        mongoTemplate.save(t5);
        mongoTemplate.save(t6);
    }

    @After
    public void tearDown() throws Exception {
        Query query1 = new Query(Criteria.where("asset").is("000000010000000000000001"));
        mongoTemplate.remove(query1, Trading.class);

        Query query2 = new Query(Criteria.where("asset").is("000000000000000000000002"));
        mongoTemplate.remove(query2, Trading.class);
    }

    @Test
    public void testTradingVolume() {
        String asset1 = "000000010000000000000001";
        long tradingVolume = tradingVolume(asset1);
        Assert.assertEquals(2L, tradingVolume);


        long tradingVolume2 = tradingVolume("000000010000000000000009");
        Assert.assertEquals(0L, tradingVolume2);

        String asset2 = "000000000000000000000002";
        long t2 = tradingVolume(asset2);
        Assert.assertEquals(333L, t2);

        long t3 = tradingVolume("000000000000000000000008");
        Assert.assertEquals(0L, t3);


    }
}