package network.asimov.mysql.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mysql.constant.DaoMessage;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoMessage;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class DaoMessageServiceTest extends DaoMessageService {
    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        dSLContext.insertInto(Tables.T_DAO_MESSAGE)
                .set(Tables.T_DAO_MESSAGE.ID, 1L)
                .set(Tables.T_DAO_MESSAGE.CATEGORY, 1)
                .set(Tables.T_DAO_MESSAGE.TYPE, 1)
                .set(Tables.T_DAO_MESSAGE.MESSAGE_POSITION, 0)
                .set(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_MESSAGE.RECEIVER, "addr1")
                .set(Tables.T_DAO_MESSAGE.ADDITIONAL_INFO, "{}")
                .set(Tables.T_DAO_MESSAGE.STATE, 1)
                .set(Tables.T_DAO_MESSAGE.CREATE_TIME, now)
                .set(Tables.T_DAO_MESSAGE.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_MESSAGE)
                .set(Tables.T_DAO_MESSAGE.ID, 2L)
                .set(Tables.T_DAO_MESSAGE.CATEGORY, 2)
                .set(Tables.T_DAO_MESSAGE.TYPE, 2)
                .set(Tables.T_DAO_MESSAGE.MESSAGE_POSITION, 1)
                .set(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_MESSAGE.RECEIVER, "addr1")
                .set(Tables.T_DAO_MESSAGE.ADDITIONAL_INFO, "{}")
                .set(Tables.T_DAO_MESSAGE.STATE, 1)
                .set(Tables.T_DAO_MESSAGE.CREATE_TIME, now)
                .set(Tables.T_DAO_MESSAGE.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_MESSAGE)
                .set(Tables.T_DAO_MESSAGE.ID, 3L)
                .set(Tables.T_DAO_MESSAGE.CATEGORY, 3)
                .set(Tables.T_DAO_MESSAGE.TYPE, 1)
                .set(Tables.T_DAO_MESSAGE.MESSAGE_POSITION, 0)
                .set(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS, "address2")
                .set(Tables.T_DAO_MESSAGE.RECEIVER, "addr1")
                .set(Tables.T_DAO_MESSAGE.ADDITIONAL_INFO, "{\"proposal_id\":1}")
                .set(Tables.T_DAO_MESSAGE.STATE, 0)
                .set(Tables.T_DAO_MESSAGE.CREATE_TIME, now)
                .set(Tables.T_DAO_MESSAGE.UPDATE_TIME, now)
                .execute();
    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_DAO_MESSAGE).where(Tables.T_DAO_MESSAGE.ID.in(1L, 2L, 3L)).execute();
    }

    @Test
    public void testSaveMessage() {
        TDaoMessage message = new TDaoMessage();
        message.setCategory(DaoMessage.Category.ModifyOrgLogo.getCode());
        message.setType(DaoMessage.Type.ReadOnly.ordinal());
        message.setContractAddress("address1");
        message.setMessagePosition(DaoMessage.Position.Both.ordinal());
        message.setReceiver(Strings.EMPTY);
        message.setAdditionalInfo("{}");
        message.setState(DaoMessage.State.Unread.ordinal());

        saveMessage(message);
    }

    @Test
    public void testListMyMessage() {
        Pair<Long, List<TDaoMessage>> p1 = listMyMessage(null, null, null, null);
        Assert.assertTrue(p1.getRight().isEmpty());

        List<String> ll = Lists.newArrayList();
        ll.add("address1");
        ll.add("address2");
        Pair<Long, List<TDaoMessage>> p2 = listMyMessage("addr1", ll, 1, 10);
        Assert.assertTrue(p2.getRight().size() > 0);
    }

    @Test
    public void testListMessageByContractAddress() {
        Pair<Long, List<TDaoMessage>> p1 = listMessageByContractAddress(null, null, null);
        Assert.assertTrue(p1.getRight().isEmpty());

        Pair<Long, List<TDaoMessage>> p2 = listMessageByContractAddress("address1", 1, 10);
        Assert.assertTrue(p2.getRight().size() > 0);
    }

    @Test
    public void testUpdateMessage() {
        // id exists
        int count1 = updateMessage(1L, 1);
        Assert.assertEquals(1, count1);

        // id not exists
        int count2 = updateMessage(11L, 1);
        Assert.assertEquals(0, count2);

        // repeat update
        int count3 = updateMessage(1L, 1);
        Assert.assertEquals(1, count3);
    }

    @Test
    public void testGetVoteMessage() {
        Optional<TDaoMessage> p1 = getVoteMessage(null, null, null);
        Assert.assertTrue(!p1.isPresent());

        Optional<TDaoMessage> p3 = getVoteMessage("addr1", "address2", 1L);
        Assert.assertTrue(p3.isPresent());

    }

}