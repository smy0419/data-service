package network.asimov.chainrpc.service;

import network.asimov.chainrpc.pojo.AssetDTO;
import network.asimov.error.BusinessException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class BalanceServiceTest extends BalanceService {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testListBalance1() {
        // parameter is null
        exception.expect(BusinessException.class);
        listBalance(null);
    }

    @Test
    public void testListBalance2() {
        // invalid address
        exception.expect(BusinessException.class);
        listBalance("0dsdsdsd");
    }

    @Test
    public void testListBalance3() {
        // address don't have any asset
        List<AssetDTO> list = listBalance("0x66c0309edea8dcd06a18c2c2d1b6bc8027ad89c812");
        Assert.assertTrue(list != null && list.isEmpty());
    }

    @Test
    public void testListBalance4() {
        // address have asset
        List<AssetDTO> list = listBalance("0x668bd8118cc510f8ccd1089bd9d5e44bdc20d6e373");
        Assert.assertTrue(list != null && !list.isEmpty());
    }

    @Test
    public void testGetAsimov() {
        Optional<AssetDTO> p = getAsimov("0x668bd8118cc510f8ccd1089bd9d5e44bdc20d6e373");
        Assert.assertTrue(p.isPresent());
        Assert.assertTrue(Long.valueOf(p.get().getValue()) > 0);
    }
}