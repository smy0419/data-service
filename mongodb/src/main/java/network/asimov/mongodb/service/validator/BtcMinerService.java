package network.asimov.mongodb.service.validator;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.validator.BtcMiner;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-11-27
 */
@Service("btcMinerService")
public class BtcMinerService extends BaseService {
    public List<BtcMiner> listBtcMinerByAddress(List<String> addressList) {
        if (addressList == null) {
            return Lists.newArrayList();
        }
        Query query = new Query(Criteria.where("address").in(addressList));
        return mongoTemplate.find(query, BtcMiner.class);
    }
}
