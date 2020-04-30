package network.asimov.mongodb.service.validator;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.GroupSum;
import network.asimov.mongodb.entity.validator.Earning;
import network.asimov.mongodb.entity.validator.EarningAsset;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
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
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2020-03-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class EarningServiceTest extends EarningService {
    long nowSecond = TimeUtil.currentSeconds();

    ObjectId id1 = ObjectId.get();
    ObjectId id2 = ObjectId.get();
    ObjectId id3 = ObjectId.get();

    @Before
    public void setUp() throws Exception {
        Earning er1 = new Earning();
        er1.setId(id1);
        er1.setAddress("addr1");

        Earning er2 = new Earning();
        er2.setId(id2);
        er2.setAddress("addr1");

        Earning er3 = new Earning();
        er3.setId(id3);
        er3.setAddress("addr2");

        EarningAsset e1 = new EarningAsset();
        e1.setAsset("asset1");
        e1.setEarningId(ObjectId.createFromLegacyFormat(1, 1, 1));
        e1.setValue(11L);
        e1.setEarningId(id1);
        e1.setTime(nowSecond);

        EarningAsset e2 = new EarningAsset();
        e2.setAsset("asset1");
        e2.setValue(21L);
        e2.setEarningId(id1);
        e2.setTime(nowSecond - 2 * TimeUtil.SECONDS_OF_DAY);

        EarningAsset e3 = new EarningAsset();
        e3.setAsset("asset2");
        e3.setValue(31L);
        e3.setEarningId(id2);
        e3.setTime(nowSecond + TimeUtil.SECONDS_OF_DAY);

        EarningAsset e4 = new EarningAsset();
        e4.setAsset("asset2");
        e4.setValue(41L);
        e4.setEarningId(id3);
        e4.setTime(nowSecond + TimeUtil.SECONDS_OF_DAY);

        EarningAsset e5 = new EarningAsset();
        e5.setAsset("asset3");
        e5.setValue(333L);
        e5.setEarningId(id3);

        EarningAsset e6 = new EarningAsset();
        e6.setAsset("asset3");
        e6.setValue(22L);
        e6.setEarningId(id3);

        mongoTemplate.save(er1);
        mongoTemplate.save(er2);
        mongoTemplate.save(er3);
        mongoTemplate.save(e1);
        mongoTemplate.save(e2);
        mongoTemplate.save(e3);
        mongoTemplate.save(e4);
        mongoTemplate.save(e5);
        mongoTemplate.save(e6);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.remove(new Query(Criteria.where("address").in("addr1", "addr2")), Earning.class);
        mongoTemplate.remove(new Query(Criteria.where("asset").in("asset1", "asset2", "asset3")), EarningAsset.class);
    }

    @Test
    public void testListTotalEarning() {
        List<GroupSum> list = listTotalEarning();
        Assert.assertTrue(list.size() > 0);
        Map<String, Long> map = list.stream().collect(Collectors.toMap(GroupSum::get_id, GroupSum::getValue));
        Assert.assertTrue(map.containsKey("asset1"));
        Assert.assertTrue(map.containsKey("asset2"));
        Assert.assertTrue(map.containsKey("asset3"));

        Assert.assertTrue(32L == map.get("asset1"));
        Assert.assertTrue(72L == map.get("asset2"));
        Assert.assertTrue(355L == map.get("asset3"));

    }

    @Test
    public void testListOneDayEarning() {
        List<GroupSum> list = listOneDayEarning();
        Assert.assertTrue(list.size() > 0);

        Map<String, Long> map = list.stream().collect(Collectors.toMap(GroupSum::get_id, GroupSum::getValue));
        Assert.assertTrue(map.containsKey("asset1"));
        Assert.assertTrue(map.containsKey("asset2"));
        Assert.assertTrue(!map.containsKey("asset3"));

        Assert.assertTrue(11L == map.get("asset1"));
        Assert.assertTrue(72L == map.get("asset2"));
    }

    @Test
    public void testListEarningByAddress() {
        List<GroupSum> list = listEarningByAddress("addr1");
        Assert.assertTrue(list.size() > 0);

        Map<String, Long> map = list.stream().collect(Collectors.toMap(GroupSum::get_id, GroupSum::getValue));
        Assert.assertTrue(map.containsKey("asset1"));
        Assert.assertTrue(map.containsKey("asset2"));
        Assert.assertTrue(!map.containsKey("asset3"));

        Assert.assertTrue(32L == map.get("asset1"));
        Assert.assertTrue(31L == map.get("asset2"));

        List<GroupSum> list2 = listEarningByAddress("addr4");
        Assert.assertEquals(0, list2.size());

    }

    @Test
    public void testListEarningByAddress1() {
        Pair<Long, List<Earning>> p = listEarningByAddress("addr1", 1, 10);
        Assert.assertTrue(p.getRight().size() > 1);

        Pair<Long, List<Earning>> p2 = listEarningByAddress("addr1", 1, 1);
        Assert.assertEquals(1, p2.getRight().size());

        Pair<Long, List<Earning>> p3 = listEarningByAddress("addr3", 1, 10);
        Assert.assertEquals(0, p3.getRight().size());
    }

    @Test
    public void testMapEarningAssetByIds() {
        List<ObjectId> ids1 = Lists.newArrayList();
        ids1.add(id1);
        ids1.add(id2);
        ids1.add(ObjectId.get());

        Pair<List<String>, Map<ObjectId, List<EarningAsset>>> p = mapEarningAssetByIds(ids1);
        Assert.assertTrue(p.getLeft().contains("asset1"));
        Assert.assertTrue(p.getLeft().contains("asset2"));

        Map<ObjectId, List<EarningAsset>> map = p.getRight();
        Assert.assertTrue(map.containsKey(id1));
        Assert.assertTrue(map.containsKey(id2));

        Assert.assertTrue(map.get(id1).size() == 2);
        Assert.assertTrue(map.get(id1).get(0).getValue() == 11L);
        Assert.assertTrue(map.get(id1).get(1).getValue() == 21L);

        Assert.assertTrue(map.get(id2).size() == 1);
        Assert.assertTrue(map.get(id2).get(0).getValue() == 31L);

        Pair<List<String>, Map<ObjectId, List<EarningAsset>>> p2 = mapEarningAssetByIds(null);
        Assert.assertTrue(p2.getRight().isEmpty());
    }

    @Test
    public void testCountEarningByAddress() {
        long count1 = countEarningByAddress("addr1");
        long count2 = countEarningByAddress("addr2");
        long count3 = countEarningByAddress("addr3");

        Assert.assertTrue(count1 == 2);
        Assert.assertTrue(count2 == 1);
        Assert.assertTrue(count3 == 0);

    }
}