package network.asimov.mongodb.service.validator;

import network.asimov.mongodb.entity.validator.ValidatorRelation;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-11-27
 */
@Service("validatorRelationService")
public class ValidatorRelationService extends BaseService {
    public List<ValidatorRelation> listValidatorRelationByAddress(String address) {
        Query query = new Query(Criteria.where("address").is(address).and("bind").is(true));
        return mongoTemplate.find(query, ValidatorRelation.class);
    }
}
