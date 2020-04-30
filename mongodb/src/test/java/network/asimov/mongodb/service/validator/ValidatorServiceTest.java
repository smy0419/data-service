package network.asimov.mongodb.service.validator;

import network.asimov.mongodb.entity.validator.Validator;
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
 * @date 2020-03-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class ValidatorServiceTest extends ValidatorService {

    @Before
    public void setUp() throws Exception {
        Validator v1 = new Validator();
        v1.setAddress("addr1");
        v1.setPlannedBlocks(111L);
        v1.setActualBlocks(111L);

        Validator v2 = new Validator();
        v2.setAddress("addr2");
        v2.setPlannedBlocks(100L);
        v2.setActualBlocks(10L);

        Validator v3 = new Validator();
        v3.setAddress("addr3");
        v3.setPlannedBlocks(1234L);
        v3.setActualBlocks(1234L);

        Validator v4 = new Validator();
        v4.setAddress("addr4");
        v4.setPlannedBlocks(1000L);
        v4.setActualBlocks(0L);

        Validator v5 = new Validator();
        v5.setAddress("addr5");
        v5.setPlannedBlocks(11L);
        v5.setActualBlocks(11L);

        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
        mongoTemplate.save(v3);
        mongoTemplate.save(v4);
        mongoTemplate.save(v5);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr1", "addr2", "addr3", "addr4", "addr5")), Validator.class);
    }

    @Test
    public void testCountValidators() {
        long count = countValidators();
        Assert.assertTrue(count > 0);
    }

    @Test
    public void testListValidatorByPage() {
        Pair<Long, List<Validator>> p = listValidatorByPage(1, 10);
        Assert.assertTrue(p.getRight().size() >= 5);

        Pair<Long, List<Validator>> p2 = listValidatorByPage(1, 1);
        Assert.assertTrue(p2.getRight().size() == 1);

    }

    @Test
    public void testGetByAddress() {
        Optional<Validator> p1 = getByAddress("addr1");
        Assert.assertTrue(p1.isPresent());

        Optional<Validator> p2 = getByAddress(null);
        Assert.assertTrue(!p2.isPresent());

        Optional<Validator> p3 = getByAddress("addrx");
        Assert.assertTrue(!p3.isPresent());
    }

    @Test
    public void testListValidator() {
        List<Validator> list = listValidator();
        Assert.assertTrue(list.size() >= 5);
    }
}