package network.asimov.mongodb.service.foundation;

import network.asimov.mongodb.entity.foundation.BalanceSheet;
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

/**
 * @author sunmengyuan
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class BalanceSheetServiceTest extends BalanceSheetService {

    @Before
    public void setUp() throws Exception {
        BalanceSheet b1 = new BalanceSheet();
        b1.setAddress("addr1");

        BalanceSheet b2 = new BalanceSheet();
        b2.setAddress("addr2");

        BalanceSheet b3 = new BalanceSheet();
        b3.setAddress("addr3");

        BalanceSheet b4 = new BalanceSheet();
        b4.setAddress("addr1");

        mongoTemplate.save(b1);
        mongoTemplate.save(b2);
        mongoTemplate.save(b3);
        mongoTemplate.save(b4);
    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("address").in("addr1", "addr2", "addr3"));
        mongoTemplate.remove(query, BalanceSheet.class);
    }

    @Test
    public void testListBalanceSheet() {
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<BalanceSheet>> p = listBalanceSheet(index, limit);
        Assert.assertTrue(p.getRight().size() > 0);
    }
}