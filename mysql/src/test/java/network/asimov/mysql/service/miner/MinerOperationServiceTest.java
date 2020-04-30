package network.asimov.mysql.service.miner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TMinerOperation;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class MinerOperationServiceTest extends MinerOperationService {
    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 1L)
                .set(Tables.T_MINER_OPERATION.ROUND, 1L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash1")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 1)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "addr1")
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 2L)
                .set(Tables.T_MINER_OPERATION.ROUND, 2L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash2")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 2)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "addr1")
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 3L)
                .set(Tables.T_MINER_OPERATION.ROUND, 2L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash3")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 3)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":2}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "addr2")
                .set(Tables.T_MINER_OPERATION.TX_STATUS, (byte) 2)
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, 4L)
                .set(Tables.T_MINER_OPERATION.TX_STATUS, (byte) 2)
                .set(Tables.T_MINER_OPERATION.ROUND, 3L)
                .set(Tables.T_MINER_OPERATION.TX_HASH, "hash4")
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) 3)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":1}")
                .set(Tables.T_MINER_OPERATION.OPERATOR, "addr2")
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();
    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_MINER_OPERATION).where(Tables.T_MINER_OPERATION.ID.in(1L, 2L, 3L, 4L)).execute();
    }

    @Test
    public void testInsert() {
        Map<String, Object> args = new HashMap<>(4);
        args.put("operationType", 2);
        args.put("additionalInfoMap", Maps.newHashMap());
        args.put("operator", "addr1");
        args.put("round", 1L);
        long id = insert(args);
        Assert.assertTrue(id != 0);

        drop(id);
    }

    @Test
    public void testModifyTxHash() {
        modifyTxHash(1, "new_tx_hash");
    }

    @Test
    public void testDrop() {
        drop(1);
    }

    @Test
    public void testListTMinerOperationByType() {
        Pair<Long, List<TMinerOperation>> p1 = listTMinerOperationByType(-1, null, null);
        Assert.assertTrue(p1.getRight().isEmpty());

        Pair<Long, List<TMinerOperation>> p2 = listTMinerOperationByType(3, 1, 10);
        Assert.assertTrue(p2.getRight().size() == 2);
    }

    @Test
    public void testListTMinerOperationByAddressAndType() {
        Pair<Long, List<TMinerOperation>> p1 = listTMinerOperationByAddressAndType(null, -1, null, null);
        Assert.assertTrue(p1.getRight().isEmpty());

        Pair<Long, List<TMinerOperation>> p2 = listTMinerOperationByAddressAndType("addr2", 3, 1, 10);
        Assert.assertTrue(p2.getRight().size() == 2);
    }

    @Test
    public void testMapTMinerOperationByTxHash() {
        Map<String, TMinerOperation> map1 = mapTMinerOperationByTxHash(null);
        Assert.assertTrue(map1.isEmpty());

        List<String> ll = Lists.newArrayList();
        ll.add("hash1");
        ll.add("hash2");
        ll.add("hash1111");

        Map<String, TMinerOperation> map2 = mapTMinerOperationByTxHash(ll);
        Assert.assertTrue(map2.containsKey("hash1"));
        Assert.assertTrue(map2.containsKey("hash2"));
        Assert.assertTrue(!map2.containsKey("hash1111"));
    }

    @Test
    public void testApplyExist() {
        boolean flag1 = applyExist(-1, null, -1);
        Assert.assertTrue(flag1 == false);

        boolean flag2 = applyExist(1, "addr1", 2);
        Assert.assertTrue(flag2 == true);

        boolean flag3 = applyExist(1, "addr2", 1);
        Assert.assertTrue(flag3 == false);

        boolean flag4 = applyExist(2, "addr2", 3);
        Assert.assertTrue(flag4 == false);
    }

    @Test
    public void testApplyExist1() {
        boolean flag1 = applyExist(-1, -1, null, -1);
        Assert.assertTrue(flag1 == false);

        boolean flag2 = applyExist(3, 1, "addr2", 3);
        Assert.assertTrue(flag2 == false);

        boolean flag3 = applyExist(3, 1, "addr3", 2);
        Assert.assertTrue(flag3 == false);

        boolean flag4 = applyExist(2, 11, "addr2", 3);
        Assert.assertTrue(flag4 == false);
    }

    @Test
    public void testGetTMinerOperationByTxHash() {
        Optional<TMinerOperation> p1 = getTMinerOperationByTxHash(null);
        Assert.assertTrue(!p1.isPresent());

        Optional<TMinerOperation> p2 = getTMinerOperationByTxHash("hash1");
        Assert.assertTrue(p2.isPresent());

        Optional<TMinerOperation> p3 = getTMinerOperationByTxHash("hash1111");
        Assert.assertTrue(!p3.isPresent());
    }

    @Test
    public void testListAssetProposal() {
        List<TMinerOperation> l1 = listAssetProposal(-1);
        Assert.assertTrue(l1.isEmpty());

        List<TMinerOperation> l2 = listAssetProposal(1);
        Assert.assertTrue(l2.size() == 1);

        List<TMinerOperation> l3 = listAssetProposal(3);
        Assert.assertTrue(l3.size() == 2);

        List<TMinerOperation> l4 = listAssetProposal(4);
        Assert.assertTrue(l4.isEmpty());
    }

    @Test
    public void testListUnknownTransactions() {
        List<String> ll = listUnknownTransactions();
        Assert.assertTrue(ll.contains("hash1"));
        Assert.assertTrue(ll.contains("hash2"));
        Assert.assertTrue(!ll.contains("hash3"));
        Assert.assertTrue(!ll.contains("hash4"));
    }

    @Test
    public void testUpdateTxStatusByTxHashList() {
        updateTxStatusByTxHashList(null);
        List<String> ll = Lists.newArrayList();
        ll.add("hash1");
        ll.add("hash2");
        ll.add("hash111");

        updateTxStatusByTxHashList(ll);
    }
}