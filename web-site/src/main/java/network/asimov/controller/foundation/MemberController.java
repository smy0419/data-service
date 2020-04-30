package network.asimov.controller.foundation;

import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.mongodb.entity.foundation.Member;
import network.asimov.mongodb.service.foundation.MemberService;
import network.asimov.mongodb.service.foundation.ProposalService;
import network.asimov.mongodb.service.foundation.VoteService;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.foundation.MemberBooleanView;
import network.asimov.response.foundation.MemberView;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-21
 */
@CrossOrigin
@RestController("foundationMemberController")
@Api(tags = "foundation")
@RequestMapping(path = "/foundation/member", produces = RequestConstants.CONTENT_TYPE_JSON)
public class MemberController {
    @Resource(name = "foundationMemberService")
    private MemberService memberService;

    @Resource(name = "foundationProposalService")
    private ProposalService proposalService;

    @Resource(name = "foundationVoteService")
    private VoteService voteService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @ApiOperation(value = "List foundation member")
    @PostMapping(path = "/list", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<PageView<MemberView>> listMember(@RequestBody @Validated PurePageQuery pageQuery) {
        List<MemberView> memberViewList = Lists.newArrayList();

        Pair<Long, List<Member>> pair = memberService.listMember(pageQuery.getPage().getIndex(), pageQuery.getPage().getLimit());

        Map<String, Long> proposalCountMap = proposalService.getProposalCountMap();

        Map<String, Long> voteCountMap = voteService.getVoteCountMap();

        List<Account> accountList = daoAccountService.listAll(memberService.listAddress());
        Map<String, Account> accountMap = accountList.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        for (Member member : pair.getRight()) {
            Account account = accountMap.get(member.getAddress());

            String address = member.getAddress();
            memberViewList.add(MemberView.builder()
                    .member(PersonView.builder()
                            .name(account != null ? account.getName() : "")
                            .address(address)
                            .build())
                    .voteCount(voteCountMap.get(address) != null ? voteCountMap.get(address) : 0)
                    .proposalCount(proposalCountMap.get(address) != null ? proposalCountMap.get(address) : 0)
                    .build());

        }
        PageView<MemberView> pageView = PageView.of(pair.getLeft(), memberViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Query foundation member's position")
    @PostMapping(path = "/position")
    public ResultView<MemberBooleanView> getMemberInfo(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        Optional<Member> member = memberService.findInServiceMember(address);
        List<Account> userDTOList = daoAccountService.listAll(Lists.newArrayList(address));
        MemberBooleanView memberPositionView = MemberBooleanView.builder()
                .member(member.isPresent() ? true : false)
                .name(userDTOList.size() > 0 ? userDTOList.get(0).getName() : "")
                .build();

        return ResultView.ok(memberPositionView);
    }
}
