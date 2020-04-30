package network.asimov.mongodb.service.dorg;

import network.asimov.mongodb.entity.dorg.Vote;
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
public class VoteServiceTest extends VoteService {

    @Before
    public void setUp() throws Exception {
        Vote v1 = new Vote();
        v1.setContractAddress("address1");
        v1.setVoteId(1L);
        v1.setVoter("user1");

        Vote v2 = new Vote();
        v2.setContractAddress("address1");
        v2.setVoteId(1L);
        v2.setVoter("user2");

        Vote v3 = new Vote();
        v3.setContractAddress("address1");
        v3.setVoteId(1L);
        v3.setVoter("user3");

        Vote v4 = new Vote();
        v4.setContractAddress("address1");
        v4.setVoteId(2L);
        v4.setVoter("user1");

        Vote v5 = new Vote();
        v5.setContractAddress("address2");
        v5.setVoteId(1L);
        v5.setVoter("user1");

        Vote v6 = new Vote();
        v6.setContractAddress("address2");
        v6.setVoteId(2L);
        v6.setVoter("user2");

        mongoTemplate.save(v1);
        mongoTemplate.save(v2);
        mongoTemplate.save(v3);
        mongoTemplate.save(v4);
        mongoTemplate.save(v5);
        mongoTemplate.save(v6);
    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("contract_address").in("address1", "address2"));
        mongoTemplate.remove(query, Vote.class);
    }

    @Test
    public void testListVoteByProposalId() {
        long voteId1 = 1;
        String addr1 = "address1";
        List<Vote> list = listVoteByProposalId(voteId1, addr1);
        Assert.assertEquals(3, list.size());

        long voteId2 = 2;
        List<Vote> lis2 = listVoteByProposalId(voteId2, addr1);
        Assert.assertEquals(1, lis2.size());

        long voteId3 = 3;
        List<Vote> lis3 = listVoteByProposalId(voteId3, addr1);
        Assert.assertEquals(0, lis3.size());

        String addr2 = "address2";
        List<Vote> lis4 = listVoteByProposalId(voteId1, addr2);
        Assert.assertEquals(1, lis4.size());

    }
}