package network.asimov.mysql.service.foundation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TFoundationOperation;
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
public class FoundationOperationServiceTest extends FoundationOperationService {
    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() {
        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 1L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash1")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 1)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "addr1")
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 2L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash2")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 2)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "addr1")
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 3L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash3")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 3)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":2}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "addr2")
                .set(Tables.T_FOUNDATION_OPERATION.TX_STATUS, (byte) 2)
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, 4L)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, "hash4")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) 3)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, "{\"proposal_id\":1}")
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, "addr2")
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();
    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_FOUNDATION_OPERATION).where(Tables.T_FOUNDATION_OPERATION.ID.in(1L, 2L, 3L, 4L)).execute();
    }

    @Test
    public void testInsert() {
        Map<String, Object> args = new HashMap<>(4);
        args.put("operationType", 12);
        args.put("additionalInfoMap", Maps.newHashMap());
        args.put("operator", "addr1");
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
    public void testListFoundationOperationByType() {
        Pair<Long, List<TFoundationOperation>> p1 = listFoundationOperationByType(-1, null, null);
        Assert.assertTrue(p1.getRight().isEmpty());

        Pair<Long, List<TFoundationOperation>> p2 = listFoundationOperationByType(3, 1, 10);
        Assert.assertTrue(p2.getRight().size() == 2);
    }

    @Test
    public void testListFoundationOperationByAddressAndType() {
        Pair<Long, List<TFoundationOperation>> p1 = listFoundationOperationByAddressAndType(null, -1, null, null);
        Assert.assertTrue(p1.getRight().isEmpty());

        Pair<Long, List<TFoundationOperation>> p2 = listFoundationOperationByAddressAndType("addr2", 3, 1, 10);
        Assert.assertTrue(p2.getRight().size() == 2);
    }

    @Test
    public void testListByOperateType() {
        List<TFoundationOperation> l1 = listByOperateType(-1);
        Assert.assertTrue(l1.isEmpty());

        List<TFoundationOperation> l2 = listByOperateType(1);
        Assert.assertTrue(l2.size() == 1);
    }

    @Test
    public void testMapTFoundationOperationByTxHash() {
        Map<String, TFoundationOperation> map1 = mapTFoundationOperationByTxHash(null);
        Assert.assertTrue(map1.isEmpty());

        List<String> ll = Lists.newArrayList();
        ll.add("hash1");
        ll.add("hash2");
        ll.add("hash1111");

        Map<String, TFoundationOperation> map2 = mapTFoundationOperationByTxHash(ll);
        Assert.assertTrue(map2.containsKey("hash1"));
        Assert.assertTrue(map2.containsKey("hash2"));
        Assert.assertTrue(!map2.containsKey("hash1111"));
    }

    @Test
    public void testGetTFoundationOperationByTxHash() {
        Optional<TFoundationOperation> p1 = getTFoundationOperationByTxHash(null);
        Assert.assertTrue(!p1.isPresent());

        Optional<TFoundationOperation> p2 = getTFoundationOperationByTxHash("hash1");
        Assert.assertTrue(p2.isPresent());

        Optional<TFoundationOperation> p3 = getTFoundationOperationByTxHash("hash1111");
        Assert.assertTrue(!p3.isPresent());
    }

    @Test
    public void testApplyExist() {
        boolean flag1 = applyExist(-1, null, -1);
        Assert.assertTrue(flag1 == false);

        boolean flag2 = applyExist(1, "addr2", 3);
        Assert.assertTrue(flag2 == true);

        // because tx_status == 2
        boolean flag3 = applyExist(2, "addr2", 3);
        Assert.assertTrue(flag3 == false);

    }

    @Test
    public void testListUnknownTransactions() {
        List<String> ll = listUnknownTransactions();
        Assert.assertTrue(ll.contains("hash1"));
        Assert.assertTrue(ll.contains("hash2"));
        Assert.assertTrue(!ll.contains("hash3"));
        Assert.assertTrue(ll.contains("hash4"));
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