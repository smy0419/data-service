package network.asimov.mysql.service.dorg;

import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoAsset;
import network.asimov.util.TimeUtil;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author sunmengyuan
 * @date 2020-03-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class DaoAssetServiceTest extends DaoAssetService {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        dSLContext.insertInto(Tables.T_DAO_ASSET)
                .set(Tables.T_DAO_ASSET.ID, 1L)
                .set(Tables.T_DAO_ASSET.TX_HASH, "hash1")
                .set(Tables.T_DAO_ASSET.ASSET, "asset1")
                .set(Tables.T_DAO_ASSET.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_ASSET.DESCRIPTION, "first asset")
                .set(Tables.T_DAO_ASSET.NAME, "asset name")
                .set(Tables.T_DAO_ASSET.LOGO, "asset logo")
                .set(Tables.T_DAO_ASSET.SYMBOL, "asset symbol")
                .set(Tables.T_DAO_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_ASSET.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ASSET)
                .set(Tables.T_DAO_ASSET.ID, 2L)
                .set(Tables.T_DAO_ASSET.TX_HASH, "hash2")
                .set(Tables.T_DAO_ASSET.ASSET, "asset2")
                .set(Tables.T_DAO_ASSET.CONTRACT_ADDRESS, "address2")
                .set(Tables.T_DAO_ASSET.DESCRIPTION, "second asset")
                .set(Tables.T_DAO_ASSET.NAME, "asset name2")
                .set(Tables.T_DAO_ASSET.LOGO, "asset logo2")
                .set(Tables.T_DAO_ASSET.SYMBOL, "asset symbol2")
                .set(Tables.T_DAO_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_ASSET.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ASSET)
                .set(Tables.T_DAO_ASSET.ID, 3L)
                .set(Tables.T_DAO_ASSET.TX_HASH, "hash3")
                .set(Tables.T_DAO_ASSET.ASSET, "asset3")
                .set(Tables.T_DAO_ASSET.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_ASSET.DESCRIPTION, "third asset")
                .set(Tables.T_DAO_ASSET.NAME, "asset name3")
                .set(Tables.T_DAO_ASSET.LOGO, "asset logo3")
                .set(Tables.T_DAO_ASSET.SYMBOL, "asset symbol3")
                .set(Tables.T_DAO_ASSET.CREATE_TIME, now)
                .set(Tables.T_DAO_ASSET.UPDATE_TIME, now)
                .execute();
    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_DAO_ASSET).where(Tables.T_DAO_ASSET.ID.in(1L, 2L, 3L, 4L)).execute();
    }

    @Test
    public void testSaveAsset() {
        TDaoAsset asset = new TDaoAsset();
        asset.setContractAddress("address4");
        asset.setAsset("asset4");
        asset.setLogo("asset logo4");
        asset.setName("asset name4");
        asset.setSymbol("asset symbol4");
        asset.setTxHash("hash4");

        saveAsset(asset);

        long count = countOrgAsset("address4");
        Assert.assertTrue(count > 0);

        dSLContext.delete(Tables.T_DAO_ASSET).where(Tables.T_DAO_ASSET.TX_HASH.eq("hash4")).execute();

    }

    @Test
    public void testCountOrgAsset() {
        long count1 = countOrgAsset(null);
        Assert.assertEquals(0, count1);

        long count2 = countOrgAsset("address1");
        Assert.assertEquals(2, count2);

        long count3 = countOrgAsset("address2");
        Assert.assertEquals(1, count3);

        long count4 = countOrgAsset("address3");
        Assert.assertEquals(0, count4);
    }
}