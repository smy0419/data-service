package network.asimov.redis.service;

import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.redis.constant.RedisKey;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author zhangjing
 * @date 2020/5/25
 */
@Service("transactionCacheService")
public class TransactionCacheService {
    @Autowired
    private StringRedisTemplate redisStringTemplate;

    public long getTxCount() {
        String value = redisStringTemplate.opsForValue().get(RedisKey.TRANSACTION_COUNT);
        return StringUtils.isEmpty(value) ? 0L: Long.parseLong(value);
    }

    public Pair<Long,List<String>> queryTransactionCache(String key, Integer index, Integer limit) {
        if (index == null) {
            throw BusinessException.builder().message("index can't be null").errorCode(ErrorCode.PARAMETER_INVALID).build();
        }

        if (limit == null) {
            throw BusinessException.builder().message("limit can't be null").errorCode(ErrorCode.PARAMETER_INVALID).build();
        }

        if (index == 0 || index < 0) {
            throw BusinessException.builder().message("index must greater than or equal to 1").errorCode(ErrorCode.PARAMETER_INVALID).build();
        }

        if (limit == 0 || limit < 0) {
            throw BusinessException.builder().message("limit must greater than or equal to 1").errorCode(ErrorCode.PARAMETER_INVALID).build();
        }

        if (limit > 10) {
            throw BusinessException.builder().message("limit must less than or equal to 10").errorCode(ErrorCode.PARAMETER_INVALID).build();
        }

        long count = redisStringTemplate.opsForList().size(key);
        if (count == 0) {
            return ImmutablePair.of(0L, new ArrayList<>());
        }
        List<String> list = redisStringTemplate.opsForList().range(key, (index - 1) * limit, index * limit - 1);
        return ImmutablePair.of(count, list);
    }

    public Optional<Long> getTransactionHeight(String txHash) {
        String value = redisStringTemplate.opsForValue().get(txHash);
        return StringUtils.isEmpty(value) ? Optional.empty(): Optional.of(Long.parseLong(value));
    }
}
