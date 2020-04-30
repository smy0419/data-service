package network.asimov.mongodb.service.ascan;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author sunmengyuan
 * @date 2020-03-16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class AddressAssetBalanceServiceTest extends AddressAssetBalanceService {
    @Autowired
    protected MongoTemplate mongoTemplate;

    @Test
    public void countHoldersByAsset1() {

        long count1 = countHoldersByAsset(null);
        Assert.assertTrue(count1 == 0);

        long count2 = countHoldersByAsset("ssss");
        Assert.assertTrue(count2 == 0);

        long count3 = countHoldersByAsset("000000000000000000000000");
        Assert.assertTrue(count3 > 0);
    }

}