package network.asimov.chainrpc.service.contract;

import network.asimov.chainrpc.constant.ChainConstant;
import network.asimov.error.BusinessException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author sunmengyuan
 * @date 2020-03-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class SystemContractServiceTest extends SystemContractService {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetAbi1() {
        String abi = getAbi(ChainConstant.GENESIS_ORGANIZATION_ADDRESS);
        Assert.assertTrue(!abi.isEmpty());

        String abi2 = getAbi(ChainConstant.CONSENSUS_MANAGEMENT_ADDRESS);
        System.out.println(abi2);
    }

    @Test
    public void testGetAbi2() {
        // expect throw exception when address is not exists
        exception.expect(BusinessException.class);
        getAbi("address");
    }

    @Test
    public void testGetAbi3() {
        // expect throw exception when address is null
        exception.expect(BusinessException.class);
        getAbi(null);
    }
}