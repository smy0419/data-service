package network.asimov.mongodb.service.miner;

import network.asimov.mongodb.entity.miner.Member;
import network.asimov.mongodb.entity.miner.Round;
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
public class MemberServiceTest extends MemberService {

    @Before
    public void setUp() throws Exception {
        Member m1 = new Member();
        m1.setAddress("addr1");
        m1.setRound(111111L);

        Member m2 = new Member();
        m2.setAddress("addr2");
        m2.setRound(111111L);

        Member m3 = new Member();
        m3.setAddress("addr3");
        m3.setRound(111111L);

        Member m4 = new Member();
        m4.setAddress("addr1");
        m4.setRound(333333L);

        Member m5 = new Member();
        m5.setAddress("addr3");
        m5.setRound(333333L);

        mongoTemplate.save(m1);
        mongoTemplate.save(m2);
        mongoTemplate.save(m3);
        mongoTemplate.save(m4);
        mongoTemplate.save(m5);

        Round r = new Round();
        r.setRound(111111L);
        mongoTemplate.save(r);

    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr1", "addr2", "addr3")), Member.class);
        mongoTemplate.remove(new Query(Criteria.where("round").is(111111L)), Round.class);
        mongoTemplate.remove(new Query(Criteria.where("round").is(333333L)), Round.class);
    }

    @Test
    public void testGetCurrentMiningMember() {
        Optional<Member> p1 = getCurrentMiningMember("addr1");
        Assert.assertTrue(p1.isPresent());

        Optional<Member> p2 = getCurrentMiningMember("addr4");
        Assert.assertTrue(!p2.isPresent());
    }

    @Test
    public void testExist() {
        boolean flag1 = exist("addr1");
        Assert.assertTrue(flag1);

        boolean flag2 = exist("addr2");
        Assert.assertTrue(flag2);
    }

    @Test
    public void testListCurrentMiningMember() {
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<Member>> p = listCurrentMiningMember(index, limit);
        Assert.assertEquals(3, p.getRight().size());
    }

    @Test
    public void testListMemberByRound() {
        List<Member> list1 = listMemberByRound(111111L);
        Assert.assertTrue(list1.size() > 0);

        List<Member> list2 = listMemberByRound(222222L);
        Assert.assertTrue(list2.size() == 0);

        List<Member> list3 = listMemberByRound(333333L);
        Assert.assertTrue(list3.size() > 0);
    }
}