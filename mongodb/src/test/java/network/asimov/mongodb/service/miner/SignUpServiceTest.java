package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.Round;
import network.asimov.mongodb.entity.miner.SignUp;
import network.asimov.util.TimeUtil;
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
 * @date 2020-03-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class SignUpServiceTest extends SignUpService {
    long currentTime = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        Round r1 = new Round();
        r1.setRound(33333L);
        r1.setStartTime(currentTime - 30 * TimeUtil.SECONDS_OF_DAY);
        r1.setEndTime(currentTime - 10 * TimeUtil.SECONDS_OF_DAY);

        SignUp s1 = new SignUp();
        s1.setAddress("addr1");
        s1.setProduced(11L);
        s1.setRound(22222L);

        SignUp s2 = new SignUp();
        s2.setAddress("addr1");
        s2.setProduced(22L);
        s2.setRound(33333L);

        SignUp s3 = new SignUp();
        s3.setAddress("addr1");
        s3.setProduced(99L);
        s3.setRound(33334L);

        SignUp s4 = new SignUp();
        s4.setAddress("addr2");
        s4.setProduced(10L);
        s4.setRound(33334L);

        SignUp s5 = new SignUp();
        s5.setAddress("addr3");
        s5.setProduced(88L);
        s5.setRound(33333L);

        SignUp s6 = new SignUp();
        s6.setAddress("addr3");
        s6.setProduced(188L);
        s6.setRound(33334L);

        mongoTemplate.save(r1);

        mongoTemplate.save(s1);
        mongoTemplate.save(s2);
        mongoTemplate.save(s3);
        mongoTemplate.save(s4);
        mongoTemplate.save(s5);
        mongoTemplate.save(s6);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("round").is(33333L)), Round.class);
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr1", "addr2", "addr3")), SignUp.class);
    }

    @Test
    public void testListNextRoundSignUp() {
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<SignUp>> p = listNextRoundSignUp(index, limit);
        List<SignUp> list = p.getRight();
        Assert.assertTrue(!list.isEmpty());
        Assert.assertTrue(list.size() > 0);
        Assert.assertTrue(list.get(0).getProduced() >= list.get(1).getProduced());
        Assert.assertTrue(list.get(1).getProduced() >= list.get(2).getProduced());
    }
}