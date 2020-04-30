package network.asimov.mongodb.service.foundation;

import network.asimov.mongodb.entity.foundation.Member;
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
public class MemberServiceTest extends MemberService {

    @Before
    public void setUp() throws Exception {
        Member m1 = new Member();
        m1.setAddress("addr1");
        m1.setInService(true);

        Member m2 = new Member();
        m2.setAddress("addr2");
        m2.setInService(true);

        Member m3 = new Member();
        m3.setAddress("addr3");
        m3.setInService(false);

        Member m4 = new Member();
        m4.setAddress("addr4");
        m4.setInService(false);

        mongoTemplate.save(m1);
        mongoTemplate.save(m2);
        mongoTemplate.save(m3);
        mongoTemplate.save(m4);
    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("address").in("addr1", "addr2", "addr3", "addr4"));
        mongoTemplate.remove(query, Member.class);
    }

    @Test
    public void testListMember() {
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<Member>> p1 = listMember(index, limit);
        Assert.assertTrue(p1.getRight().size() > 0);
    }

    @Test
    public void testListAddress() {
        List<String> list = listAddress();
        Assert.assertTrue(list.contains("addr1"));
        Assert.assertTrue(list.contains("addr2"));
        Assert.assertTrue(!list.contains("addr3"));
        Assert.assertTrue(!list.contains("addr4"));
    }

    @Test
    public void testFindInServiceMember() {
        Optional<Member> p1 = findInServiceMember("addr1");
        Assert.assertTrue(p1.isPresent());

        Optional<Member> p2 = findInServiceMember("addr2");
        Assert.assertTrue(p2.isPresent());

        Optional<Member> p3 = findInServiceMember("addr3");
        Assert.assertTrue(!p3.isPresent());
    }
}