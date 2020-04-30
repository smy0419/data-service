package network.asimov.chainrpc.service;

import network.asimov.chainrpc.pojo.ContractSourceDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class ContractServiceTest extends ContractService {

    @Test
    public void testGetSource() {
        Optional<ContractSourceDTO> p1 = getSource(1, "dao");
        Assert.assertTrue(p1.isPresent());
        Assert.assertTrue(!p1.get().getAbi().isEmpty());

        // category or template name not exists
        Optional<ContractSourceDTO> p2 = getSource(0, "dao");
        Assert.assertTrue(!p2.isPresent());
    }
}