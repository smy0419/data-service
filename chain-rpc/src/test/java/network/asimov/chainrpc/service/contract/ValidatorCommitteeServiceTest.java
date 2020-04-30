package network.asimov.chainrpc.service.contract;

import network.asimov.chainrpc.pojo.MinerBlockDTO;
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
public class ValidatorCommitteeServiceTest extends ValidatorCommitteeService {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetMinerBlocks1() {
        // which is not a miner address.
        MinerBlockDTO m1 = getMinerBlocks("0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812");
        Assert.assertTrue(m1 != null);
        Assert.assertTrue(m1.getPlannedBlocks() == 0);
        Assert.assertTrue(m1.getActualBlocks() == 0);

    }

    @Test
    public void testGetMinerBlocks2() {
        exception.expect(NullPointerException.class);
        getMinerBlocks(null);
    }

    @Test
    public void testGetMinerBlocks3() {
        exception.expect(IndexOutOfBoundsException.class);
        getMinerBlocks("0");
    }

    @Test
    public void testGetMinerBlocks4() {
        // invalid address
        exception.expect(BusinessException.class);
        getMinerBlocks("0x1321231321313");
    }

    @Test
    public void testGetMinerBlocks5() {
        // a valid miner address in tech network
        MinerBlockDTO m1 = getMinerBlocks("0x668bd8118cc510f8ccd1089bd9d5e44bdc20d6e373");
        Assert.assertTrue(m1 != null);
        Assert.assertTrue(m1.getPlannedBlocks() > 0);
        Assert.assertTrue(m1.getActualBlocks() > 0);
    }


}