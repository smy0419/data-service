package network.asimov.mysql.service;

import com.google.common.collect.Lists;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.TableLike;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-09-26
 */
public abstract class BaseService {
    @Autowired
    protected DSLContext dSLContext;

    protected <T> Pair<Long, List<T>> queryByPage(TableLike<?> table,
                                                  Condition condition,
                                                  Integer index,
                                                  Integer limit,
                                                  OrderField<?> orderField,
                                                  Class<T> clazz) {
        long count = dSLContext.selectCount()
                .from(table)
                .where(condition)
                .fetchOneInto(Long.class);

        if (count == 0) {
            return ImmutablePair.of(0L, Lists.newArrayList());
        }

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

        List<T> list = dSLContext.select()
                .from(table)
                .where(condition)
                .orderBy(orderField)
                .limit(limit)
                .offset((index - 1) * limit)
                .fetchInto(clazz);
        return ImmutablePair.of(count, list);
    }
}
