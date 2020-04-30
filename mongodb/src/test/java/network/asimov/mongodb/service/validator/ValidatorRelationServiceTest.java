package network.asimov.mongodb.service.validator;

import network.asimov.mongodb.entity.validator.ValidatorRelation;
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
public class ValidatorRelationServiceTest extends ValidatorRelationService {

    @Before
    public void setUp() throws Exception {
        ValidatorRelation v1 = new ValidatorRelation();
        v1.setAddress("addr1");
        v1.setBind(true);

        ValidatorRelation v2 = new ValidatorRelation();
        v2.setAddress("addr2");
        v2.setBind(false);

        ValidatorRelation v3 = new ValidatorRelation();
        v3.setAddress("addr3");
        v3.setBind(true);

        ValidatorRelation v4 = new ValidatorRelation();
        v4.setAddress("addr4");
        v4.setBind(false);

        ValidatorRelation v5 = new ValidatorRelation();
        v5.setAddress("addr1");
        v5.setBind(false);

        ValidatorRelation v6 = new ValidatorRelation();
        v6.setAddress("addr1");
        v6.setBind(true);

        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
        mongoTemplate.save(v3);
        mongoTemplate.save(v4);
        mongoTemplate.save(v5);
        mongoTemplate.save(v6);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr1", "addr2", "addr3", "addr4")), ValidatorRelation.class);
    }

    @Test
    public void testListValidatorRelationByAddress() {
        List<ValidatorRelation> list1 = listValidatorRelationByAddress("addr1");
        List<ValidatorRelation> list2 = listValidatorRelationByAddress("addr2");
        List<ValidatorRelation> list3 = listValidatorRelationByAddress("addr3");
        List<ValidatorRelation> list4 = listValidatorRelationByAddress("addr4");

        Assert.assertEquals(2, list1.size());
        Assert.assertEquals(0, list2.size());
        Assert.assertEquals(1, list3.size());
        Assert.assertEquals(0, list4.size());
    }
}