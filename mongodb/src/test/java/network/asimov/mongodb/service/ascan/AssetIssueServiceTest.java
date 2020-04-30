package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.AssetIssue;
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

/**
 * @author sunmengyuan
 * @date 2020-03-18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class AssetIssueServiceTest extends AssetIssueService {

    @Before
    public void setUp() throws Exception {
        AssetIssue assetIssue = new AssetIssue();
        assetIssue.setAsset("000000010000000010000001");
        assetIssue.setValue(999L);
        mongoTemplate.save(assetIssue);

        AssetIssue assetIssue2 = new AssetIssue();
        assetIssue2.setAsset("000000010000000010000001");
        assetIssue2.setValue(1L);
        mongoTemplate.save(assetIssue2);

        AssetIssue assetIssue3 = new AssetIssue();
        assetIssue3.setAsset("000000000000000010000001");
        assetIssue3.setValue(1L);
        mongoTemplate.save(assetIssue3);

    }


    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("asset").is("000000000000000010000001"));
        mongoTemplate.remove(query, AssetIssue.class);

        Query query2 = new Query(Criteria.where("asset").is("000000010000000010000001"));
        mongoTemplate.remove(query2, AssetIssue.class);
    }

    @Test
    public void listAssetIssue1() {
        String assetId = null;
        Query query = new Query(Criteria.where("asset").is(assetId));
        List<AssetIssue> list = mongoTemplate.find(query, AssetIssue.class);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void listAssetIssue2() {
        String assetId = "000000000000000010000001";
        Query query = new Query(Criteria.where("asset").is(assetId));
        List<AssetIssue> list = mongoTemplate.find(query, AssetIssue.class);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void sumAssetIssue1() {
        String assetId = "000000010000000010000001";
        long amount = sumAssetIssue(assetId);
        Assert.assertEquals(2, amount);
    }

    @Test
    public void sumAssetIssue2() {
        String assetId = "112312312312312312";
        long amount = sumAssetIssue(assetId);
        Assert.assertEquals(0, amount);
    }

    @Test
    public void queryAssetIssue1() {
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<AssetIssue>> p = queryAssetIssue(index, limit, null);
        Assert.assertEquals(0, p.getRight().size());
    }

    @Test
    public void queryAssetIssue2() {
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<AssetIssue>> p = queryAssetIssue(index, limit, "000000010000000010000001");
        Assert.assertEquals(2, p.getRight().size());
    }
}