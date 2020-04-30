package network.asimov.mongodb.service.dorg;

import network.asimov.mongodb.entity.dorg.Member;
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
        m1.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        m1.setAddress("aaa");
        m1.setRole(0);
        m1.setStatus(1);

        Member m2 = new Member();
        m2.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        m2.setAddress("bbb");
        m2.setRole(1);
        m2.setStatus(0);

        Member m3 = new Member();
        m3.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        m3.setAddress("ccc");
        m3.setRole(1);
        m3.setStatus(1);

        Member m4 = new Member();
        m4.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        m4.setAddress("ddd");
        m4.setRole(1);
        m4.setStatus(2);

        Member m5 = new Member();
        m5.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        m5.setAddress("eee");
        m5.setRole(0);
        m5.setStatus(2);

        Member m6 = new Member();
        m6.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664dbb");
        m6.setAddress("fff");
        m6.setRole(0);
        m6.setStatus(1);

        Member m7 = new Member();
        m7.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664dbb");
        m7.setAddress("aaa");
        m7.setRole(1);
        m7.setStatus(1);

        mongoTemplate.save(m1);
        mongoTemplate.save(m2);
        mongoTemplate.save(m3);
        mongoTemplate.save(m4);
        mongoTemplate.save(m5);
        mongoTemplate.save(m6);
        mongoTemplate.save(m7);
    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("contract_address").in("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664dbb"));
        mongoTemplate.remove(query, Member.class);
    }

    @Test
    public void testCountMember() {
        long count1 = countMember("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertEquals(2, count1);

        long count2 = countMember("0x63d8665bab8e955ab1ae2854aa0d1afdd955664dbb");
        Assert.assertEquals(2, count2);

    }

    @Test
    public void testListMember() {
        List<Member> list = listMember("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertEquals(2, list.size());

        List<Member> list2 = listMember("0x73d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertEquals(0, list2.size());
    }

    @Test
    public void testListMemberByAddress() {
        String address1 = "aaa";
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<Member>> p1 = listMemberByAddress(address1, index, limit);
        Assert.assertEquals(2, p1.getRight().size());

        String address2 = "ccc";
        Pair<Long, List<Member>> p2 = listMemberByAddress(address2, index, limit);
        Assert.assertEquals(1, p2.getRight().size());

        String address3 = "qqq";
        Pair<Long, List<Member>> p3 = listMemberByAddress(address3, index, limit);
        Assert.assertEquals(0, p3.getRight().size());

    }

    @Test
    public void testGetInServiceMemberByAddress() {
        Optional<Member> p1 = getInServiceMemberByAddress("aaa", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertTrue(p1.isPresent());

        Optional<Member> p2 = getInServiceMemberByAddress("qqq", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertTrue(!p2.isPresent());

        Optional<Member> p3 = getInServiceMemberByAddress("aaa", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664dbb");
        Assert.assertTrue(p3.isPresent());

        Optional<Member> p4 = getInServiceMemberByAddress("aaa", "0x67d8665bab8e955ab1ae2854aa0d1afdd955664dbb");
        Assert.assertTrue(!p4.isPresent());
    }

    @Test
    public void testListMyOrg() {
        List<String> list1 = listMyOrg("aaa");
        Assert.assertEquals(2, list1.size());

        List<String> list2 = listMyOrg("bbb");
        Assert.assertEquals(0, list2.size());
    }

    @Test
    public void testGetMemberByAddress() {
        Optional<Member> p1 = getMemberByAddress("aaa", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertTrue(p1.isPresent());

        Optional<Member> p2 = getMemberByAddress("bbb", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertTrue(p2.isPresent());

    }
}