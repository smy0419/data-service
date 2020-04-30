package network.asimov.mongodb.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
@Service("daoOrganizationService")
public class OrganizationService extends BaseService {
    public Optional<Organization> getOrganizationByAddress(String contractAddress) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").is(contractAddress));
        Organization organization = mongoTemplate.findOne(query, Organization.class);
        return Optional.ofNullable(organization);

    }

    public List<Organization> listOrganizationByAddressList(List<String> addressList) {
        if (addressList == null || addressList.isEmpty()) {
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").in(addressList));
        return mongoTemplate.find(query, Organization.class);
    }

    public Map<String, Organization> mapOrganization(List<String> addressList) {
        List<Organization> list = listOrganizationByAddressList(addressList);
        return list.stream().collect(Collectors.toMap(Organization::getContractAddress, organization -> organization));
    }
}
