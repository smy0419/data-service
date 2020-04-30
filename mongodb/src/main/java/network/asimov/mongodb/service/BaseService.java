package network.asimov.mongodb.service;

import com.google.common.collect.Lists;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-09-20
 */
public abstract class BaseService {
    @Autowired
    protected MongoTemplate mongoTemplate;

//    protected <T> Pair<Long, List<T>> queryByPage(Integer limit,
//                                                  long time,
//                                                  long offset,
//                                                  boolean next,
//                                                  Class<T> clazz) {
//        if (limit == null) {
//            throw BusinessException.builder().message("limit can't be null").errorCode(ErrorCode.PARAMETER_INVALID).build();
//        }
//
//        if (limit == 0 || limit < 0) {
//            throw BusinessException.builder().message("limit must greater than or equal to 1").errorCode(ErrorCode.PARAMETER_INVALID).build();
//        }
//
//        Query query = new Query();
//        if (time > 0) {
//            query.addCriteria(Criteria.where("time").gte(time).andOperator(Criteria.where("time").lt(time + TimeUtil.SECONDS_OF_DAY)));
//        }
//
//        long count = mongoTemplate.count(query, clazz);
//        if (count == 0) {
//            return ImmutablePair.of(0L, Lists.newArrayList());
//        }
//
//        Query dataQuery = new Query();
//        if (next) {
//            if (offset > 0) {
//                if (time > 0) {
//                    dataQuery.addCriteria(Criteria.where("time").gte(time).andOperator(Criteria.where("time").lt(offset)));
//                } else {
//                    dataQuery.addCriteria(Criteria.where("time").lt(offset));
//                }
//            }
//        } else {
//            if (offset <= 0) {
//                throw BusinessException.builder().message("previous view must provide time").errorCode(ErrorCode.PARAMETER_INVALID).build();
//            }
//            if (time > 0) {
//                dataQuery.addCriteria(Criteria.where("time").gt(offset).andOperator(Criteria.where("time").lt(time + TimeUtil.SECONDS_OF_DAY)));
//            } else {
//                dataQuery.addCriteria(Criteria.where("time").gt(offset));
//            }
//        }
//
//        dataQuery.limit(limit).with(new Sort(Sort.Direction.DESC, "time"));
//        List<T> list = mongoTemplate.find(dataQuery, clazz);
//
//        return ImmutablePair.of(count, list);
//    }

    protected <T> Pair<Long, List<T>> queryByPage(Integer index,
                                                  Integer limit,
                                                  Query query,
                                                  Class<T> clazz,
                                                  Sort.Direction sortDirection,
                                                  String... sortProperty) {
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

        long count = mongoTemplate.count(query, clazz);

        if (count == 0) {
            return ImmutablePair.of(0L, Lists.newArrayList());
        }
        if (sortDirection != null) {
            query.skip((index - 1) * limit).limit(limit).with(new Sort(sortDirection, sortProperty));
        } else {
            query.skip((index - 1) * limit).limit(limit);
        }

        List<T> list = mongoTemplate.find(query, clazz);

        return ImmutablePair.of(count, list);
    }

    protected <T> Pair<Long, List<T>> queryByPageTopN(Integer maxRecordsNumber,
                                                  Integer index,
                                                  Integer limit,
                                                  Query query,
                                                  Class<T> clazz,
                                                      Sort.Direction sortDirection,
                                                      String... sortProperty) {
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

        if (index * limit > maxRecordsNumber) {
            throw BusinessException.builder().message("limit or index is too big").errorCode(ErrorCode.PARAMETER_INVALID).build();
        }

        long count = mongoTemplate.count(query, clazz);

        if (count == 0) {
            return ImmutablePair.of(0L, Lists.newArrayList());
        }

        count = count > maxRecordsNumber ? maxRecordsNumber : count;

        query.skip((index - 1) * limit).limit(limit).with(new Sort(sortDirection, sortProperty));
        List<T> list = mongoTemplate.find(query, clazz);

        return ImmutablePair.of(count, list);

    }

    protected <T> List<T> findAggregateList(Aggregation aggregation, String collectionName, Class<T> clazz) {
        AggregationResults<T> aggregate = this.mongoTemplate.aggregate(aggregation, collectionName, clazz);
        return aggregate.getMappedResults();
    }
}
