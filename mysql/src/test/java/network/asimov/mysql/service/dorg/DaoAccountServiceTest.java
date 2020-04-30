package network.asimov.mysql.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.error.BusinessException;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoAccount;
import network.asimov.mysql.pojo.Account;
import network.asimov.util.TimeUtil;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-03-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class DaoAccountServiceTest extends DaoAccountService {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    long nowSecond = TimeUtil.currentSeconds();

    @Before
    public void setUp() throws Exception {
        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 1L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, "addr1")
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "name1")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, nowSecond)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, nowSecond)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 2L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, "addr2")
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "name2")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, nowSecond)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, nowSecond)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.ID, 3L)
                .set(Tables.T_DAO_ACCOUNT.ADDRESS, "addr3")
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, "name3")
                .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, nowSecond)
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, nowSecond)
                .execute();
    }

    @After
    public void tearDown() throws Exception {
        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.in(1L, 2L, 3L)).execute();
    }

    @Test
    public void testFindOrAdd1() {
        // address exists
        TDaoAccount account1 = findOrAdd("addr1");
        Assert.assertTrue(account1 != null);

        // address not exits
        TDaoAccount account2 = findOrAdd("addr111");
        Assert.assertTrue(account2 != null);

        dSLContext.delete(Tables.T_DAO_ACCOUNT).where(Tables.T_DAO_ACCOUNT.ID.in(account2.getId())).execute();


    }

    @Test
    public void testFindOrAdd2() {
        // address is null
        exception.expect(BusinessException.class);
        exception.expectMessage("invalid parameter");
        findOrAdd(null);
    }

    @Test
    public void testFindByAddress1() {
        TDaoAccount account1 = findByAddress("addr1");
        Assert.assertTrue(account1 != null);
        TDaoAccount account2 = findByAddress("addr1111");
        Assert.assertTrue(account2 == null);
    }

    @Test
    public void testFindByAddress2() {
        exception.expect(BusinessException.class);
        exception.expectMessage("invalid parameter");
        findByAddress(null);
    }


    @Test
    public void testUpdateAccount() {
        // address exists
        TDaoAccount account1 = new TDaoAccount();
        account1.setAddress("addr1");
        account1.setAvatar("new avatar");
        account1.setNickName("new nick name");
        account1.setUpdateTime(nowSecond);

        int count = updateAccount(account1);
        Assert.assertEquals(1, count);
        TDaoAccount newAccount = findByAddress(account1.getAddress());
        Assert.assertEquals("new avatar", newAccount.getAvatar());
        Assert.assertEquals("new nick name", newAccount.getNickName());

        // address not exists
        TDaoAccount account2 = new TDaoAccount();
        account2.setAddress("sdsdsdsdsdsd");
        account2.setAvatar("sdsdsdsdsdsd");
        account2.setNickName("new nick name");
        account2.setUpdateTime(nowSecond);

        int count2 = updateAccount(account2);
        Assert.assertEquals(0, count2);
    }

    @Test
    public void testListAll1() {
        List<Account> list = listAll(null);
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void testListAll2() {
        List<String> addressList = Lists.newArrayList();
        addressList.add("addr1");// exists
        addressList.add("addr2");// exists
        addressList.add("addr3");// exists
        addressList.add("addr4");// not exists
        addressList.add("");// not exists
        List<Account> list = listAll(addressList);
        Assert.assertTrue(!list.isEmpty());
        Assert.assertEquals(3, list.size());
    }
}