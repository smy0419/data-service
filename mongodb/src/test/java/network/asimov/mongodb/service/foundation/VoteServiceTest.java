package network.asimov.mongodb.service.foundation;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.foundation.Proposal;
import network.asimov.mongodb.entity.foundation.Vote;
import network.asimov.mongodb.pojo.VoteDTO;
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
import java.util.Map;

/**
 * @author sunmengyuan
 * @date 2020-03-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class VoteServiceTest extends VoteService {

    @Before
    public void setUp() throws Exception {
        Vote v1 = new Vote();
        v1.setProposalId(1L);
        v1.setVoter("addr1");
        v1.setTxHash("hash1");
        v1.setDecision(true);

        Vote v2 = new Vote();
        v2.setProposalId(1L);
        v2.setVoter("addr2");
        v2.setTxHash("hash2");
        v2.setDecision(false);

        Vote v3 = new Vote();
        v3.setProposalId(1L);
        v3.setVoter("addr3");
        v3.setTxHash("hash3");
        v3.setDecision(true);

        Vote v4 = new Vote();
        v4.setProposalId(2L);
        v4.setVoter("addr1");
        v4.setTxHash("hash4");
        v4.setDecision(true);

        Vote v5 = new Vote();
        v5.setProposalId(2L);
        v5.setVoter("addr4");
        v5.setTxHash("hash5");
        v5.setDecision(true);

        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
        mongoTemplate.save(v3);
        mongoTemplate.save(v4);
        mongoTemplate.save(v5);

        Proposal p1 = new Proposal();
        p1.setProposalId(1L);
        p1.setStatus(1);
        p1.setTime(System.currentTimeMillis() / 1000);
        p1.setAddress("addr999");
        p1.setProposalType(1);
        p1.setTxHash("hashxxxx");

        Proposal p2 = new Proposal();
        p2.setProposalId(2L);
        p2.setStatus(0);
        p2.setTime(System.currentTimeMillis() / 1000);
        p2.setAddress("addr888");
        p2.setProposalType(1);
        p2.setTxHash("hashqqq");

        mongoTemplate.save(p1);
        mongoTemplate.save(p2);


    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("tx_hash").in("hash1", "hash2", "hash3", "hash4", "hash5")), Vote.class);
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr888", "addr999")), Proposal.class);
    }

    @Test
    public void testGetVoteCountMap() {
        Map<String, Long> map = getVoteCountMap();
        Assert.assertTrue(2L == map.get("addr1"));
        Assert.assertTrue(1L == map.get("addr2"));
        Assert.assertTrue(1L == map.get("addr3"));
        Assert.assertTrue(1L == map.get("addr4"));
        Assert.assertTrue(!map.containsKey("addr5"));
    }

    @Test
    public void testListVoteDTOByTxHash() {
        List<String> list1 = Lists.newArrayList();
        list1.add("hash1");
        list1.add("hash2");
        list1.add("hash4");
        list1.add("hash9");// not exists

        List<VoteDTO> result = listVoteDTOByTxHash(list1);
        Assert.assertEquals(3, result.size());

        List<String> list2 = Lists.newArrayList();
        list2.add("hash11");
        list2.add("hash22");
        list2.add("hash33");

        List<VoteDTO> result2 = listVoteDTOByTxHash(list2);
        Assert.assertTrue(result2.isEmpty());

        List<VoteDTO> result3 = listVoteDTOByTxHash(null);
        Assert.assertTrue(result3.isEmpty());

    }

    @Test
    public void testListVoteByProposalId() {
        long id1 = 1;
        List<Vote> list1 = listVoteByProposalId(id1);
        Assert.assertTrue(list1.size() > 0);

        long id2 = 2;
        List<Vote> list2 = listVoteByProposalId(id2);
        Assert.assertTrue(list2.size() > 0);

        long id3 = 33333;
        List<Vote> list3 = listVoteByProposalId(id3);
        Assert.assertTrue(list3.size() == 0);

    }
}