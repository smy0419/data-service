package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.Round;
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
 * @date 2020-03-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class RoundServiceTest extends RoundService {
    long currentTime = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        Round r1 = new Round();
        r1.setRound(111111L);
        r1.setStartTime(currentTime - 30 * TimeUtil.SECONDS_OF_DAY);
        r1.setEndTime(currentTime - 10 * TimeUtil.SECONDS_OF_DAY);

        Round r2 = new Round();
        r2.setRound(222222L);
        r2.setStartTime(currentTime - 10 * TimeUtil.SECONDS_OF_DAY);
        r2.setEndTime(currentTime);

        Round r3 = new Round();
        r3.setRound(333333L);
        r3.setStartTime(currentTime + 10 * TimeUtil.SECONDS_OF_DAY);
        r3.setEndTime(currentTime + 20 * TimeUtil.SECONDS_OF_DAY);

        mongoTemplate.save(r1);
        mongoTemplate.save(r2);
        mongoTemplate.save(r3);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("round").in(111111L, 222222L, 333333L)), Round.class);
    }

    @Test
    public void testGetCurrentRound() {
        Round r = getCurrentRound();
        Assert.assertTrue(r.getRound() == 333333L);
    }
}