package network.asimov.mysql.service.ecology;

import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TChainNode;
import network.asimov.util.TimeUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-03-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class ChainNodeServiceTest extends ChainNodeService {
    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        dSLContext.insertInto(Tables.T_CHAIN_NODE)
                .set(Tables.T_CHAIN_NODE.ID, 1L)
                .set(Tables.T_CHAIN_NODE.IP, "10.102.0.14")
                .set(Tables.T_CHAIN_NODE.CITY, "hangzhou")
                .set(Tables.T_CHAIN_NODE.SUBDIVISION, "qqq")
                .set(Tables.T_CHAIN_NODE.COUNTRY, "china")
                .set(Tables.T_CHAIN_NODE.LONGITUDE, "123.4444")
                .set(Tables.T_CHAIN_NODE.LATITUDE, "222.4444")
                .set(Tables.T_CHAIN_NODE.CREATE_TIME, now)
                .set(Tables.T_CHAIN_NODE.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_CHAIN_NODE)
                .set(Tables.T_CHAIN_NODE.ID, 2L)
                .set(Tables.T_CHAIN_NODE.IP, "101.1.1.1")
                .set(Tables.T_CHAIN_NODE.CITY, "shanghai")
                .set(Tables.T_CHAIN_NODE.SUBDIVISION, "2323")
                .set(Tables.T_CHAIN_NODE.COUNTRY, "china")
                .set(Tables.T_CHAIN_NODE.LONGITUDE, "124.4444")
                .set(Tables.T_CHAIN_NODE.LATITUDE, "242.4444")
                .set(Tables.T_CHAIN_NODE.CREATE_TIME, now)
                .set(Tables.T_CHAIN_NODE.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_CHAIN_NODE)
                .set(Tables.T_CHAIN_NODE.ID, 3L)
                .set(Tables.T_CHAIN_NODE.IP, "9.13.0.11")
                .set(Tables.T_CHAIN_NODE.CITY, "NewYork")
                .set(Tables.T_CHAIN_NODE.SUBDIVISION, "sdsdsd")
                .set(Tables.T_CHAIN_NODE.COUNTRY, "America")
                .set(Tables.T_CHAIN_NODE.LONGITUDE, "323.4444")
                .set(Tables.T_CHAIN_NODE.LATITUDE, "2323.4444")
                .set(Tables.T_CHAIN_NODE.CREATE_TIME, now)
                .set(Tables.T_CHAIN_NODE.UPDATE_TIME, now)
                .execute();
    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_CHAIN_NODE).where(Tables.T_CHAIN_NODE.ID.in(1L, 2L, 3L)).execute();
    }

    @Test
    public void testListChainNode() {
        List<TChainNode> ll = listChainNode();
        Assert.assertTrue(ll.size() == 3);
    }
}