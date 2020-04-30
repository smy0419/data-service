package network.asimov.chainrpc.service;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
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
public class TransactionServiceTest extends TransactionService {

    @Test
    public void testGetMemPoolTransactions() {
        List<String> l1 = getMemPoolTransactions(null);
        Assert.assertTrue(l1.isEmpty());

        List<String> hashes = Lists.newArrayList();
        hashes.add("0dsdsdsdsd");
        hashes.add("2323232323");
        hashes.add("sdsdsdsdds");

        List<String> l2 = getMemPoolTransactions(hashes);
        Assert.assertTrue(l2.isEmpty());
    }
}