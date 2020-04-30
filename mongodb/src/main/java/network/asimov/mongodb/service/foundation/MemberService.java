package network.asimov.mongodb.service.foundation;

import network.asimov.mongodb.entity.foundation.Member;
import network.asimov.mongodb.service.BaseService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-21
 */
@Service("foundationMemberService")
public class MemberService extends BaseService {
    public Pair<Long, List<Member>> listMember(Integer index, Integer limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("in_service").is(true));
        return queryByPage(index, limit, query, Member.class, Sort.Direction.ASC, "time");
    }

    public List<String> listAddress() {
        Query query = new Query();
        query.addCriteria(Criteria.where("in_service").is(true));
        List<Member> memberList = mongoTemplate.find(query, Member.class);
        return memberList.stream().map(Member::getAddress).collect(Collectors.toList());

    }

    public Optional<Member> findInServiceMember(String address) {
        Query query = new Query();
        query.addCriteria(Criteria.where("address").is(address));
        query.addCriteria(Criteria.where("in_service").is(true));
        Member member = mongoTemplate.findOne(query, Member.class);

        return Optional.ofNullable(member);
    }
}
