package network.asimov.mongodb.service.ascan;

import network.asimov.mongodb.entity.ascan.Block;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-23
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,mysql-dev,chainrpc-dev,mongodb-dev")
public class BlockServiceTest extends BlockService {

    @Test
    public void testGetHandledBlockHeight() {
        Optional<Long> p = getHandledBlockHeight();
        Assert.assertTrue(p.isPresent());
        Assert.assertTrue(p.get() >= 0);
    }

    @Test
    public void testQueryBlockByPage1() {
        Integer index = 1;
        Integer limit = 10;
        Pair<Long, List<Block>> p = queryBlockByPage(index, limit);
        Assert.assertTrue(p.getRight().size() > 0);
    }

    @Test
    public void testGetBlock1() {
        Optional<Block> p = getBlock(11L);
        Assert.assertTrue(p.isPresent());
    }

    @Test
    public void testGetBlock2() {
        Block block = mongoTemplate.findOne(new Query(), Block.class);
        Optional<Block> p1 = getBlock(block.getHash());
        Assert.assertTrue(p1.isPresent());
    }
}