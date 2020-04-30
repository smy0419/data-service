package network.asimov.mongodb.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.dorg.Proposal;
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

import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class ProposalServiceTest extends ProposalService {

    @Before
    public void setUp() throws Exception {
        Proposal p1 = new Proposal();
        p1.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");
        p1.setProposalId(1L);
        p1.setStatus(0);
        p1.setTxHash("hash1");
        p1.setEndTime(System.currentTimeMillis() / 1000 - TimeUtil.SECONDS_OF_DAY);

        Proposal p2 = new Proposal();
        p2.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");
        p2.setProposalId(2L);
        p2.setStatus(1);
        p2.setTxHash("hash2");
        p2.setEndTime(System.currentTimeMillis() / 1000 + 2 * TimeUtil.SECONDS_OF_DAY);

        Proposal p3 = new Proposal();
        p3.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");
        p3.setProposalId(3L);
        p3.setStatus(2);
        p3.setTxHash("hash3");
        p3.setEndTime(System.currentTimeMillis() - 2 * TimeUtil.SECONDS_OF_DAY);

        Proposal p4 = new Proposal();
        p4.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");
        p4.setProposalId(4L);
        p4.setStatus(3);
        p4.setTxHash("hash4");
        p4.setEndTime(TimeUtil.currentSeconds() - 2 * TimeUtil.SECONDS_OF_DAY);

        Proposal p5 = new Proposal();
        p5.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        p5.setProposalId(5L);
        p5.setStatus(0);
        p5.setTxHash("hash5");

        Proposal p6 = new Proposal();
        p6.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        p6.setProposalId(6L);
        p6.setStatus(3);
        p6.setTxHash("hash6");
        p6.setEndTime(TimeUtil.currentSeconds() + 2 * TimeUtil.SECONDS_OF_DAY);

        mongoTemplate.save(p1);
        mongoTemplate.save(p2);
        mongoTemplate.save(p3);
        mongoTemplate.save(p4);
        mongoTemplate.save(p5);
        mongoTemplate.save(p6);

    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("hash").in("hash1", "hash2", "hash3", "hash4", "hash5", "hash6"));
        mongoTemplate.remove(query, Proposal.class);
    }

    @Test
    public void testListProposalByIds() {
        String contractAddress1 = "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1";
        String contractAddress2 = "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2";

        List<Long> ids = Lists.newArrayList();
        ids.add(1L);
        ids.add(2L);
        ids.add(11L);
        ids.add(3L);
        List<Proposal> proposalList = listProposalByIds(contractAddress1, ids);
        Assert.assertTrue(proposalList.size() > 0);

        List<Proposal> proposalList2 = listProposalByIds(contractAddress2, ids);
        Assert.assertEquals(0, proposalList2.size());

        List<Proposal> proposalList3 = listProposalByIds(contractAddress1, null);
        Assert.assertEquals(0, proposalList3.size());


    }

    @Test
    public void testGetProposalById() {
        String contractAddress = "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1";
        Long proposalId = 1L;
        Optional<Proposal> p1 = getProposalById(proposalId, contractAddress);
        Assert.assertTrue(p1.isPresent());

        Long proposalId2 = 2L;
        Optional<Proposal> p2 = getProposalById(proposalId2, contractAddress);
        Assert.assertTrue(p2.isPresent());

        Long proposalId3 = 22L;
        Optional<Proposal> p3 = getProposalById(proposalId3, contractAddress);
        Assert.assertTrue(!p3.isPresent());

        String contractAddress2 = "0x73d8665bab8e955ab1ae2854aa0d1afdd955664db1";
        Optional<Proposal> p4 = getProposalById(proposalId, contractAddress2);
        Assert.assertTrue(!p4.isPresent());
    }

    @Test
    public void testGetProposalByHash() {
        String hash1 = "hash1";
        Optional<Proposal> p1 = getProposalByHash(hash1);
        Assert.assertTrue(p1.isPresent());

        String hash2 = "hash333";
        Optional<Proposal> p2 = getProposalByHash(hash2);
        Assert.assertTrue(!p2.isPresent());

        Optional<Proposal> p3 = getProposalByHash(null);
        Assert.assertTrue(!p3.isPresent());

    }

    @Test
    public void testUpdateExpiredProposal() {
        List<Proposal> list = updateExpiredProposal();
        Assert.assertTrue(list.size() > 0);
    }
}