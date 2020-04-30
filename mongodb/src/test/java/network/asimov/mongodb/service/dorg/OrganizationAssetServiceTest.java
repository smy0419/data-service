package network.asimov.mongodb.service.dorg;

import network.asimov.mongodb.entity.dorg.OrganizationAsset;
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

/**
 * @author sunmengyuan
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class OrganizationAssetServiceTest extends OrganizationAssetService {

    @Before
    public void setUp() throws Exception {
        OrganizationAsset o1 = new OrganizationAsset();
        o1.setAsset("000000010000000000000001");
        o1.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");

        OrganizationAsset o2 = new OrganizationAsset();
        o2.setAsset("000000000000000000000001");
        o2.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");

        OrganizationAsset o3 = new OrganizationAsset();
        o3.setAsset("000000010000000000000002");
        o3.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");

        OrganizationAsset o4 = new OrganizationAsset();
        o4.setAsset("000000000000000000000001");
        o4.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3");

        mongoTemplate.save(o1);
        mongoTemplate.save(o2);
        mongoTemplate.save(o3);
        mongoTemplate.save(o4);

    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("contract_address").in("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3"));
        mongoTemplate.remove(query, OrganizationAsset.class);
    }

    @Test
    public void testListOrgAssetByAddress() {
        List<OrganizationAsset> l1 = listOrgAssetByAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        Assert.assertEquals(3, l1.size());

        List<OrganizationAsset> l2 = listOrgAssetByAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3");
        Assert.assertEquals(1, l2.size());

        List<OrganizationAsset> l3 = listOrgAssetByAddress("0x73d8665bab8e955ab1ae2854aa0d1afdd955664db3");
        Assert.assertEquals(0, l3.size());
    }
}