package network.asimov.mysql.service.dorg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoOperation;
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
public class DaoOperationServiceTest extends DaoOperationService {
    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        dSLContext.insertInto(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.ID, 1L)
                .set(Tables.T_DAO_OPERATION.TX_HASH, "hash1")
                .set(Tables.T_DAO_OPERATION.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_OPERATION.OPERATION_TYPE, (byte) 1)
                .set(Tables.T_DAO_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_DAO_OPERATION.OPERATOR, "addr1")
                .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) 0)
                .set(Tables.T_DAO_OPERATION.CREATE_TIME, now)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.ID, 2L)
                .set(Tables.T_DAO_OPERATION.TX_HASH, "hash2")
                .set(Tables.T_DAO_OPERATION.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_OPERATION.OPERATION_TYPE, (byte) 2)
                .set(Tables.T_DAO_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_DAO_OPERATION.OPERATOR, "addr1")
                .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) 0)
                .set(Tables.T_DAO_OPERATION.CREATE_TIME, now)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.ID, 3L)
                .set(Tables.T_DAO_OPERATION.CONTRACT_ADDRESS, "address2")
                .set(Tables.T_DAO_OPERATION.OPERATION_TYPE, (byte) 3)
                .set(Tables.T_DAO_OPERATION.ADDITIONAL_INFO, "{}")
                .set(Tables.T_DAO_OPERATION.OPERATOR, "addr2")
                .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) 2)
                .set(Tables.T_DAO_OPERATION.CREATE_TIME, now)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, now)
                .execute();

    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_OPERATION).where(Tables.T_DAO_OPERATION.ID.in(1L, 2L, 3L)).execute();
    }

    @Test
    public void testModifyTxHash() {
        modifyTxHash(1, "hash1");
    }

    @Test
    public void testDrop() {
        drop(1);
    }

    @Test
    public void testInsert() {
        Map<String, Object> args = new HashMap<>(4);
        args.put("operationType", 12);
        args.put("additionalInfoMap", Maps.newHashMap());
        args.put("operator", "addr1");
        args.put("contractAddress", "addr2");
        long id = insert(args);
        Assert.assertTrue(id != 0);

        drop(id);
    }

    @Test
    public void testListOperationByOrg() {
        Pair<Long, List<TDaoOperation>> p1 = listOperationByOrg(null, null, null);
        Assert.assertTrue(p1.getRight().isEmpty());

        Pair<Long, List<TDaoOperation>> p2 = listOperationByOrg("address1", 1, 10);
        Assert.assertTrue(p2.getRight().size() == 2);
    }

    @Test
    public void testGetOperationByTxHash() {
        Optional<TDaoOperation> p1 = getOperationByTxHash(null);
        Assert.assertTrue(!p1.isPresent());

        Optional<TDaoOperation> p2 = getOperationByTxHash("hash1");
        Assert.assertTrue(p2.isPresent());

        Optional<TDaoOperation> p3 = getOperationByTxHash("xxxxx");
        Assert.assertTrue(!p3.isPresent());
    }

    @Test
    public void testListUnknownTransactions() {
        List<String> ll = listUnknownTransactions();
        Assert.assertTrue(ll.contains("hash1"));
        Assert.assertTrue(ll.contains("hash2"));
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