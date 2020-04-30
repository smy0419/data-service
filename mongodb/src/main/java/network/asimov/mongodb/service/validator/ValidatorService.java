package network.asimov.mongodb.service.validator;

import network.asimov.mongodb.entity.validator.Validator;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Service("validatorService")
public class ValidatorService extends BaseService {
    public long countValidators() {
        Query query = new Query();
        return mongoTemplate.count(query, Validator.class);
    }

    public Pair<Long, List<Validator>> listValidatorByPage(Integer index, Integer limit) {
        Query query = new Query();
        return queryByPage(index, limit, query, Validator.class, Sort.Direction.DESC, "time");
    }

    public Optional<Validator> getByAddress(String address) {
        Query query = new Query(Criteria.where("address").is(address));
        Validator validator = mongoTemplate.findOne(query, Validator.class);
        return Optional.ofNullable(validator);
    }

    public List<Validator> listValidator() {
        Query query = new Query();
        return mongoTemplate.find(query, Validator.class);
    }
}
