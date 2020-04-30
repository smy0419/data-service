package network.asimov.mongodb.service.dorg;

import network.asimov.mongodb.entity.dorg.OrganizationAsset;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Service("daoOrganizationAssetService")
public class OrganizationAssetService extends BaseService {
    public List<OrganizationAsset> listOrgAssetByAddress(String contractAddress) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").is(contractAddress));
        List<OrganizationAsset> assetList = mongoTemplate.find(query, OrganizationAsset.class);
        return assetList;
    }
}
