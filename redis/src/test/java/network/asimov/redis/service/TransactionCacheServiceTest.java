package network.asimov.redis.service;

import network.asimov.redis.constant.RedisKey;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-03-25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=dev,redis-dev")
public class TransactionCacheServiceTest {
   @Resource(name = "transactionCacheService")
   private TransactionCacheService transactionCacheService;

    @Test
    public void test() {
//        System.out.println(transactionCacheService.getTxCount());
//        System.out.println(transactionCacheService.queryTransactionCache(RedisKey.TRANSACTIONS,2,10));
        Optional<Long> height = transactionCacheService.getTransactionHeight("c10fe70c43e0d138e43fe047d69e269d3fc5cf5d5d06d931dcf09537ee651e70");
       if ( height.isPresent()) {
           System.out.println(height.get());
       } else {
           System.out.println("not found");
       }
    }
}