package network.asimov.mysql.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoIndivisibleAsset;
import network.asimov.util.TimeUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @author sunmengyuan
 * @date 2020-03-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class DaoIndivisibleAssetServiceTest extends DaoIndivisibleAssetService {
    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        dSLContext.insertInto(Tables.T_DAO_INDIVISIBLE_ASSET)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ID, 1L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.TX_HASH, "hash1")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET, "asset1")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID, 11L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_DESC, "11 desc")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.UPDATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_STATUS, (byte) 1)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_INDIVISIBLE_ASSET)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ID, 2L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.TX_HASH, "hash2")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET, "asset1")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID, 22L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_DESC, "22 desc")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.UPDATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_STATUS, (byte) 1)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_INDIVISIBLE_ASSET)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ID, 3L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.TX_HASH, "hash3")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET, "asset2")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.VOUCHER_ID, 123L)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_DESC, "123 desc")
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.UPDATE_TIME, now)
                .set(Tables.T_DAO_INDIVISIBLE_ASSET.ASSET_STATUS, (byte) 0)
                .execute();

    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_DAO_INDIVISIBLE_ASSET).where(Tables.T_DAO_INDIVISIBLE_ASSET.ID.in(1L, 2L, 3L)).execute();
    }

    @Test
    public void testSaveIndivisibleAsset() {
        TDaoIndivisibleAsset indivisibleAsset = new TDaoIndivisibleAsset();
        indivisibleAsset.setTxHash("hash22");
        indivisibleAsset.setContractAddress("address2");
        indivisibleAsset.setAsset("asset3");
        indivisibleAsset.setVoucherId(1234L);
        indivisibleAsset.setAssetDesc("1234 desc");
        saveIndivisibleAsset(indivisibleAsset);

        dSLContext.delete(Tables.T_DAO_INDIVISIBLE_ASSET).where(Tables.T_DAO_INDIVISIBLE_ASSET.TX_HASH.eq("hash22")).execute();

    }

    @Test
    public void testListIndivisibleAssets() {
        List<TDaoIndivisibleAsset> list1 = listIndivisibleAssets(null);
        Assert.assertTrue(list1.isEmpty());

        List<TDaoIndivisibleAsset> list2 = listIndivisibleAssets("asset1");
        Assert.assertTrue(list2.size() == 2);

        List<TDaoIndivisibleAsset> list3 = listIndivisibleAssets("asset33333");
        Assert.assertTrue(list3.isEmpty());

        List<TDaoIndivisibleAsset> list4 = listIndivisibleAssets("asset2");
        Assert.assertTrue(list4.size() == 1);
    }

    @Test
    public void testMapIndivisibleDesc() {
        Map<Long, String> map1 = mapIndivisibleDesc(null, null);
        Assert.assertTrue(map1.isEmpty());

        // asset is not exists and voucher id is null
        Map<Long, String> map2 = mapIndivisibleDesc("qqq", null);
        Assert.assertTrue(map2.isEmpty());

        // asset is exists but voucher id is null
        Map<Long, String> map3 = mapIndivisibleDesc("asset1", null);
        Assert.assertTrue(map3.isEmpty());

        List<Long> voucherIds = Lists.newArrayList();
        voucherIds.add(11L);// exists
        voucherIds.add(22L);// exists
        voucherIds.add(33L);// not exists
        Map<Long, String> map4 = mapIndivisibleDesc("asset1", voucherIds);
        Assert.assertTrue(!map4.isEmpty());
        Assert.assertTrue(map4.containsKey(11L));
        Assert.assertTrue(map4.containsKey(22L));
        Assert.assertTrue(!map4.containsKey(33L));


        // asset2 status == 0 so the map is empty
        List<Long> voucherIds2 = Lists.newArrayList();
        voucherIds2.add(11L);// not exists
        voucherIds2.add(123L);// exists
        Map<Long, String> map5 = mapIndivisibleDesc("asset2", voucherIds2);
        Assert.assertTrue(map5.isEmpty());
    }


    @Test
    public void testGetIndivisibleDesc() {
        String desc1 = getIndivisibleDesc(null, null);
        Assert.assertTrue(desc1.isEmpty());

        String desc2 = getIndivisibleDesc("asset1", 11L);
        Assert.assertTrue(desc2.equals("11 desc"));

        String desc3 = getIndivisibleDesc("asset1", 12345L);
        Assert.assertTrue(desc3.isEmpty());

    }

    @Test
    public void testCountIndivisibleAssetByVoucherId() {
        long count1 = countIndivisibleAssetByVoucherId(null, null, null);
        Assert.assertEquals(0, count1);

        long count2 = countIndivisibleAssetByVoucherId("address1", "asset1", 11L);
        Assert.assertEquals(1, count2);

        long count3 = countIndivisibleAssetByVoucherId("address1", "asset2", 123L);
        Assert.assertEquals(1, count3);
    }
}