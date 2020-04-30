package network.asimov.mongodb.service.foundation;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.foundation.Proposal;
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
import java.util.Optional;
import java.util.stream.Collectors;

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
        p1.setProposalId(1L);
        p1.setAddress("addr1");
        p1.setTxHash("hash1");

        Proposal p2 = new Proposal();
        p2.setProposalId(2L);
        p2.setAddress("addr1");
        p2.setTxHash("hash2");

        Proposal p3 = new Proposal();
        p3.setProposalId(3L);
        p3.setAddress("addr2");
        p3.setTxHash("hash3");

        Proposal p4 = new Proposal();
        p4.setProposalId(4L);
        p4.setAddress("addr3");
        p4.setTxHash("hash4");

        mongoTemplate.save(p1);
        mongoTemplate.save(p2);
        mongoTemplate.save(p3);
        mongoTemplate.save(p4);
    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("address").in("addr1", "addr2", "addr3"));
        mongoTemplate.remove(query, Proposal.class);
    }

    @Test
    public void testGetProposalCountMap() {
        Map<String, Long> map = getProposalCountMap();
        Assert.assertTrue(2L == map.get("addr1"));
        Assert.assertTrue(1L == map.get("addr2"));
        Assert.assertTrue(!map.containsKey("addr5"));
    }

    @Test
    public void testGetProposalById() {
        Optional<Proposal> p1 = getProposalById(1L);
        Assert.assertTrue(p1.isPresent());

        Optional<Proposal> p2 = getProposalById(5L);
        Assert.assertTrue(!p2.isPresent());
    }

    @Test
    public void testGetProposalByHash() {
        Optional<Proposal> p1 = getProposalByHash("hash1");
        Assert.assertTrue(p1.isPresent());

        Optional<Proposal> p2 = getProposalByHash("hash5");
        Assert.assertTrue(!p2.isPresent());
    }

    @Test
    public void testListProposalByIds() {
        List<Long> ids = Lists.newArrayList();
        ids.add(1L);
        ids.add(2L);
        ids.add(5L);

        List<Proposal> list = listProposalByIds(ids);
        List<Long> result = list.stream().map(Proposal::getProposalId).collect(Collectors.toList());
        Assert.assertTrue(result.contains(1L));
        Assert.assertTrue(result.contains(2L));
        Assert.assertTrue(!result.contains(5L));

        List<Proposal> list1 = listProposalByIds(null);
        Assert.assertTrue(list1.isEmpty());

    }

    @Test
    public void testListProposalByTxHash() {
        List<String> hashes = Lists.newArrayList();
        hashes.add("hash1");
        hashes.add("hash2");
        hashes.add("hash3");
        hashes.add("hash5");

        List<Proposal> list = listProposalByTxHash(hashes);
        List<String> result = list.stream().map(Proposal::getTxHash).collect(Collectors.toList());
        Assert.assertTrue(result.contains("hash1"));
        Assert.assertTrue(result.contains("hash2"));
        Assert.assertTrue(result.contains("hash3"));
        Assert.assertTrue(!result.contains("hash5"));

        List<Proposal> list1 = listProposalByTxHash(null);
        Assert.assertTrue(list1.isEmpty());
    }

    @Test
    public void testMapProposalByTxHash() {
        List<String> hashes = Lists.newArrayList();
        hashes.add("hash1");
        hashes.add("hash2");
        hashes.add("hash3");
        hashes.add("hash5");

        Map<String, Proposal> map = mapProposalByTxHash(hashes);
        Assert.assertTrue(map.containsKey("hash1"));
        Assert.assertTrue(map.containsKey("hash2"));
        Assert.assertTrue(map.containsKey("hash3"));
        Assert.assertTrue(!map.containsKey("hash5"));
    }
}