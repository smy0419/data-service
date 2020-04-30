package network.asimov.mongodb.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.dorg.Organization;
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
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class OrganizationServiceTest extends OrganizationService {

    @Before
    public void setUp() throws Exception {
        Organization o1 = new Organization();
        o1.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");

        Organization o2 = new Organization();
        o2.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");

        Organization o3 = new Organization();
        o3.setContractAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3");

        mongoTemplate.save(o1);
        mongoTemplate.save(o2);
        mongoTemplate.save(o3);

    }

    @After
    public void tearDown() throws Exception {
        Query query = new Query(Criteria.where("contract_address").in("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2", "0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3"));
        mongoTemplate.remove(query, Organization.class);
    }

    @Test
    public void testGetOrganizationByAddress() {
        Optional<Organization> o1 = getOrganizationByAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");
        Assert.assertTrue(o1.isPresent());

        Optional<Organization> o2 = getOrganizationByAddress("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db4");
        Assert.assertTrue(!o2.isPresent());
    }

    @Test
    public void testListOrganizationByAddressList() {
        List<String> list1 = Lists.newArrayList();
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3");
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db4");

        List<Organization> organizationList = listOrganizationByAddressList(list1);
        Assert.assertEquals(3, organizationList.size());

        List<Organization> organizationList1 = listOrganizationByAddressList(null);
        Assert.assertTrue(organizationList1.isEmpty());
    }

    @Test
    public void testMapOrganization() {
        List<String> list1 = Lists.newArrayList();
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1");
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2");
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3");
        list1.add("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db4");

        Map<String, Organization> orgMap = mapOrganization(list1);
        Assert.assertTrue(orgMap.containsKey("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db1"));
        Assert.assertTrue(orgMap.containsKey("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db2"));
        Assert.assertTrue(orgMap.containsKey("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db3"));
        Assert.assertTrue(!orgMap.containsKey("0x63d8665bab8e955ab1ae2854aa0d1afdd955664db4"));
    }
}