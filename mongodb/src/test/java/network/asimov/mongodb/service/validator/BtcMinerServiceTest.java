package network.asimov.mongodb.service.validator;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.validator.BtcMiner;
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

/**
 * @author sunmengyuan
 * @date 2020-03-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class BtcMinerServiceTest extends BtcMinerService {

    @Before
    public void setUp() throws Exception {
        BtcMiner b1 = new BtcMiner();
        b1.setAddress("addr1");
        b1.setDomain("domain1");

        BtcMiner b2 = new BtcMiner();
        b2.setAddress("addr2");
        b2.setDomain("domain2");

        BtcMiner b3 = new BtcMiner();
        b3.setAddress("addr3");
        b3.setDomain("domain3");

        mongoTemplate.save(b1);
        mongoTemplate.save(b2);
        mongoTemplate.save(b3);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr1", "addr2", "addr3")), BtcMiner.class);
    }

    @Test
    public void testListBtcMinerByAddress() {
        List<String> addrList = Lists.newArrayList();
        addrList.add("addr1");
        addrList.add("addr2");
        addrList.add("addr4");

        List<BtcMiner> list = listBtcMinerByAddress(addrList);
        Assert.assertEquals(2, list.size());

        List<BtcMiner> list2 = listBtcMinerByAddress(null);
        Assert.assertTrue(list2.isEmpty());
    }
}