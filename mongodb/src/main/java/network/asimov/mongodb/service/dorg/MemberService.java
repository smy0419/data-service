package network.asimov.mongodb.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.mongodb.entity.dorg.Member;
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
 * @date 2020-01-03
 */
@Service("daoMemberService")
public class MemberService extends BaseService {
    public long countMember(String contractAddress) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").is(contractAddress).and("status").is(Member.Status.Agreed.ordinal()));
        return mongoTemplate.count(query, Member.class);
    }

    public List<Member> listMember(String contractAddress) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").is(contractAddress).and("status").is(Member.Status.Agreed.ordinal()));
        return mongoTemplate.find(query, Member.class);
    }

    public Pair<Long, List<Member>> listMemberByAddress(String address, Integer index, Integer limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("address").is(address).and("status").is(Member.Status.Agreed.ordinal()));
        return queryByPage(index, limit, query, Member.class, Sort.Direction.DESC, "time");
    }

    public Optional<Member> getInServiceMemberByAddress(String address, String contractAddress) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").is(contractAddress).and("address").is(address).and("status").is(Member.Status.Agreed.ordinal()));
        Member member = mongoTemplate.findOne(query, Member.class);
        return Optional.ofNullable(member);
    }

    public List<String> listMyOrg(String address) {
        List<String> orgList = Lists.newArrayList();
        Query query = new Query();
        query.addCriteria(Criteria.where("address").is(address).and("status").is(Member.Status.Agreed.ordinal()));
        List<Member> memberList = mongoTemplate.find(query, Member.class);
        for (Member member : memberList) {
            orgList.add(member.getContractAddress());
        }
        return orgList;
    }

    public Optional<Member> getMemberByAddress(String address, String contractAddress) {
        Query query = new Query();
        query.addCriteria(Criteria.where("contract_address").is(contractAddress).and("address").is(address));
        Member member = mongoTemplate.findOne(query, Member.class);
        return Optional.ofNullable(member);
    }
}
