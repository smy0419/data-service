package network.asimov.mysql.service.dorg;

import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoOrganization;
import network.asimov.util.TimeUtil;
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
public class DaoOrganizationServiceTest extends DaoOrganizationService {
    long now = TimeUtil.currentSeconds();

    @Before
    public void setUp() {
        dSLContext.insertInto(Tables.T_DAO_ORGANIZATION)
                .set(Tables.T_DAO_ORGANIZATION.ID, 1L)
                .set(Tables.T_DAO_ORGANIZATION.TX_HASH, "hash1")
                .set(Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS, "address1")
                .set(Tables.T_DAO_ORGANIZATION.VOTE_CONTRACT_ADDRESS, "vote_address1")
                .set(Tables.T_DAO_ORGANIZATION.CREATOR_ADDRESS, "addr1")
                .set(Tables.T_DAO_ORGANIZATION.ORG_NAME, "org1")
                .set(Tables.T_DAO_ORGANIZATION.ORG_LOGO, "org_logo1")
                .set(Tables.T_DAO_ORGANIZATION.STATE, (byte) 1)
                .set(Tables.T_DAO_ORGANIZATION.CREATE_TIME, now)
                .set(Tables.T_DAO_ORGANIZATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ORGANIZATION)
                .set(Tables.T_DAO_ORGANIZATION.ID, 2L)
                .set(Tables.T_DAO_ORGANIZATION.TX_HASH, "hash2")
                .set(Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS, "address2")
                .set(Tables.T_DAO_ORGANIZATION.VOTE_CONTRACT_ADDRESS, "vote_address2")
                .set(Tables.T_DAO_ORGANIZATION.CREATOR_ADDRESS, "addr1")
                .set(Tables.T_DAO_ORGANIZATION.ORG_NAME, "org2")
                .set(Tables.T_DAO_ORGANIZATION.ORG_LOGO, "")
                .set(Tables.T_DAO_ORGANIZATION.STATE, (byte) 1)
                .set(Tables.T_DAO_ORGANIZATION.CREATE_TIME, now)
                .set(Tables.T_DAO_ORGANIZATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ORGANIZATION)
                .set(Tables.T_DAO_ORGANIZATION.ID, 3L)
                .set(Tables.T_DAO_ORGANIZATION.TX_HASH, "hash3")
                .set(Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS, "address3")
                .set(Tables.T_DAO_ORGANIZATION.VOTE_CONTRACT_ADDRESS, "vote_address3")
                .set(Tables.T_DAO_ORGANIZATION.CREATOR_ADDRESS, "addr2")
                .set(Tables.T_DAO_ORGANIZATION.ORG_NAME, "org3")
                .set(Tables.T_DAO_ORGANIZATION.ORG_LOGO, "org_logo3")
                .set(Tables.T_DAO_ORGANIZATION.STATE, (byte) 1)
                .set(Tables.T_DAO_ORGANIZATION.CREATE_TIME, now)
                .set(Tables.T_DAO_ORGANIZATION.UPDATE_TIME, now)
                .execute();

        dSLContext.insertInto(Tables.T_DAO_ORGANIZATION)
                .set(Tables.T_DAO_ORGANIZATION.ID, 4L)
                .set(Tables.T_DAO_ORGANIZATION.TX_HASH, "hash4")
                .set(Tables.T_DAO_ORGANIZATION.CONTRACT_ADDRESS, "address4")
                .set(Tables.T_DAO_ORGANIZATION.VOTE_CONTRACT_ADDRESS, "vote_address4")
                .set(Tables.T_DAO_ORGANIZATION.CREATOR_ADDRESS, "addr3")
                .set(Tables.T_DAO_ORGANIZATION.ORG_NAME, "org2")
                .set(Tables.T_DAO_ORGANIZATION.ORG_LOGO, "org_logo4")
                .set(Tables.T_DAO_ORGANIZATION.STATE, (byte) 0)
                .set(Tables.T_DAO_ORGANIZATION.CREATE_TIME, now)
                .set(Tables.T_DAO_ORGANIZATION.UPDATE_TIME, now)
                .execute();

    }

    @After
    public void tearDown() {
        dSLContext.delete(Tables.T_DAO_ORGANIZATION).where(Tables.T_DAO_ORGANIZATION.ID.in(1L, 2L, 3L, 4L)).execute();
    }

    @Test
    public void testSaveOrganization() {
        TDaoOrganization org = new TDaoOrganization();
        org.setOrgName("org_name_1");
        org.setCreatorAddress("org_address_1");
        org.setTxHash("org_hash_1");
        org.setState((byte) 0);
        org.setOrgLogo("org_logo_url");
        saveOrganization(org);
    }

    @Test
    public void testUpdateOrganization() {
        updateOrganization("address1", "new_org_logo");
    }

    @Test
    public void testGetOrgByContractAddress() {
        Optional<TDaoOrganization> p1 = getOrgByContractAddress("address1");
        Assert.assertTrue(p1.isPresent());

        Optional<TDaoOrganization> p2 = getOrgByContractAddress(null);
        Assert.assertTrue(!p2.isPresent());

        Optional<TDaoOrganization> p3 = getOrgByContractAddress("sdsdsd");
        Assert.assertTrue(!p3.isPresent());
    }

    @Test
    public void testListOrgByName() {
        List<TDaoOrganization> ll = listOrgByName(null);
        Assert.assertTrue(ll.isEmpty());

        List<TDaoOrganization> l2 = listOrgByName("org1");
        Assert.assertTrue(l2.get(0).getOrgName().equals("org1"));

        List<TDaoOrganization> l3 = listOrgByName("org2");
        Assert.assertTrue(l3.get(0).getOrgName().equals("org2"));
        Assert.assertTrue(l3.get(1).getOrgName().equals("org2"));
    }
}