package network.asimov.mongodb.service.miner;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.miner.Proposal;
import network.asimov.mongodb.entity.miner.Vote;
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
        v1.setProposalId(11111L);
        v1.setVoter("addr1");
        v1.setTxHash("hash1");
        v1.setDecision(true);

        Vote v2 = new Vote();
        v2.setProposalId(11111L);
        v2.setVoter("addr2");
        v2.setTxHash("hash2");
        v2.setDecision(false);

        Vote v3 = new Vote();
        v3.setProposalId(11111L);
        v3.setVoter("addr3");
        v3.setTxHash("hash3");
        v3.setDecision(true);

        Vote v4 = new Vote();
        v4.setProposalId(22222L);
        v4.setVoter("addr1");
        v4.setTxHash("hash4");
        v4.setDecision(true);

        Vote v5 = new Vote();
        v5.setProposalId(22222L);
        v5.setVoter("addr4");
        v5.setTxHash("hash5");
        v5.setDecision(true);

        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
        mongoTemplate.save(v3);
        mongoTemplate.save(v4);
        mongoTemplate.save(v5);

        Proposal p1 = new Proposal();
        p1.setProposalId(11111L);
        p1.setStatus(1);
        p1.setTime(System.currentTimeMillis() / 1000);
        p1.setAddress("addr999");
        p1.setType(1);
        p1.setTxHash("hashxxxx");

        Proposal p2 = new Proposal();
        p2.setProposalId(22222L);
        p2.setStatus(0);
        p2.setTime(System.currentTimeMillis() / 1000);
        p2.setAddress("addr888");
        p2.setType(1);
        p2.setTxHash("hashqqq");

        mongoTemplate.save(p1);
        mongoTemplate.save(p2);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("voter").in("addr1", "addr2", "addr3", "addr4")), Vote.class);
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr888", "addr999")), Proposal.class);
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

        List<VoteDTO> result2 = listVoteDTOByTxHash(null);
        Assert.assertTrue(result2.isEmpty());
    }

    @Test
    public void testListVoteByProposalId() {
        long id1 = 11111;
        List<Vote> list1 = listVoteByProposalId(id1);
        Assert.assertEquals(3, list1.size());

        long id2 = 22222;
        List<Vote> list2 = listVoteByProposalId(id2);
        Assert.assertEquals(2, list2.size());

        long id3 = 33333;
        List<Vote> list3 = listVoteByProposalId(id3);
        Assert.assertEquals(0, list3.size());

    }
}