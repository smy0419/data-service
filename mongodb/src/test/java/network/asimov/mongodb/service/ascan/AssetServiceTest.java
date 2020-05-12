package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.Asset;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
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

/**
 * @author sunmengyuan
 * @date 2020-05-09
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class AssetServiceTest extends AssetService {

	@Before
	public void setUp() throws Exception {
		Asset asset1 = new Asset();
		asset1.setName("asset1");
		asset1.setAsset("000100010000000000000001");
		asset1.setHeight(1L);
		mongoTemplate.save(asset1);

		Asset asset2 = new Asset();
		asset2.setName("asset2");
		asset2.setAsset("000100010000000000000002");
		asset2.setHeight(2L);
		mongoTemplate.save(asset2);

		Asset asset3 = new Asset();
		asset3.setName("asset3");
		asset3.setAsset("000100010000000000000003");
		asset3.setHeight(3L);
		mongoTemplate.save(asset3);
	}


	@After
	public void tearDown() throws Exception {
		Query query = new Query(Criteria.where("asset").in("000100010000000000000001", "000100010000000000000002", "000100010000000000000003"));
		mongoTemplate.remove(query, Asset.class);
	}

	@Test
	public void testGetAsset() {
		Optional<Asset> p1 = getAsset("000100010000000000000001");
		Assert.assertTrue(p1.isPresent());

		Optional<Asset> p2 = getAsset(null);
		Assert.assertTrue(!p2.isPresent());

		Optional<Asset> p3 = getAsset("sdsdsdd");
		Assert.assertTrue(!p3.isPresent());
	}

	@Test
	public void testListAsset() {
		List<Asset> list1 = listAsset(null);
		Assert.assertTrue(list1.isEmpty());

		List<String> assetIds = Lists.newArrayList();
		assetIds.add("000100010000000000000001");
		assetIds.add("000100010000000000000002");
		assetIds.add("000100010000000000000004");
		List<Asset> list2 = listAsset(assetIds);
		Assert.assertTrue(list2.size() == 2);
	}

	@Test
	public void testMapAssets() {
		Map<String, Asset> m1 = mapAssets(null);
		Assert.assertTrue(m1.isEmpty());

		List<String> assetIds = Lists.newArrayList();
		assetIds.add("000100010000000000000001");
		assetIds.add("000100010000000000000002");
		assetIds.add("000100010000000000000004");
		Map<String, Asset> m2 = mapAssets(assetIds);
		Assert.assertTrue(m2.size() == 2);

	}

	@Test
	public void testListAllAsset() {
		List<Asset> list = listAllAsset();
		Assert.assertTrue(!list.isEmpty());
	}

	@Test
	public void testListAssetByPageAndName() {
		Pair<Long, List<Asset>> p1 = listAssetByPageAndName(1, 10, "asset1");
		Assert.assertTrue(p1.getRight().size() == 1);

		Pair<Long, List<Asset>> p2 = listAssetByPageAndName(1, 10, null);
		Assert.assertTrue(p1.getRight().size() > 0);
	}
}