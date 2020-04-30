package network.asimov.controller.dorg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.mongodb.entity.dorg.Member;
import network.asimov.mongodb.service.dorg.MemberService;
import network.asimov.mysql.constant.DaoOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.request.RequestConstants;
import network.asimov.request.dorg.*;
import network.asimov.response.ResultView;
import network.asimov.response.dorg.MemberInfoView;
import network.asimov.response.dorg.MemberListView;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-12-12
 */
@CrossOrigin
@RestController("daoMemberController")
@Api(tags = {"dao"})
@RequestMapping(path = "/dao/org/member", produces = RequestConstants.CONTENT_TYPE_JSON)
public class MemberController {
    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "memberChangeCheck")
    private CheckBehavior memberChangeCheck;

    @Resource(name = "confirmMemberChangeCheck")
    private CheckBehavior confirmMemberChangeCheck;

    @Resource(name = "daoSendDeletableTransaction")
    private SendTransactionBehavior daoSendDeletableTransaction;

    @PostMapping("/list")
    @ApiOperation(value = "List organization member")
    public ResultView<MemberListView> listMember(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                 @RequestBody @Validated ContractAddressQuery contractAddressQuery) {
        List<Member> orgMemberList = memberService.listMember(contractAddressQuery.getContractAddress());
        List<Account> accountList = daoAccountService.listAll(orgMemberList.stream().map(Member::getAddress).collect(Collectors.toList()));
        Map<String, Account> accountMap = accountList.stream().collect(Collectors.toMap(Account::getAddress, account -> account));
        List<MemberInfoView> members = Lists.newArrayList();
        for (Member member : orgMemberList) {
            Account account = accountMap.get(member.getAddress());
            members.add(MemberInfoView.builder()
                    .address(member.getAddress())
                    .roleType(member.getRole())
                    .avatar(account != null ? account.getAvatar() : "")
                    .name(account != null ? account.getName() : "")
                    .build());
        }

        MemberListView memberListView = MemberListView.builder()
                .members(members)
                .build();
        return ResultView.ok(memberListView);
    }

    @PostMapping("/change/check")
    @ApiOperation(value = "Check change member")
    public ResultView checkChangeMember(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                        @RequestBody @Validated MemberChangeCheckRequest memberChangeCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateMember(address, memberChangeCheckRequest.getContractAddress(), memberChangeCheckRequest.getTargetAddress(), memberChangeCheckRequest.getChangeType());
        return memberChangeCheck.check(checkArgs);
    }

    @PostMapping("/change")
    @ApiOperation(value = "Change member")
    public ResultView changeMember(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                   @RequestBody @Validated MemberChangeRequest memberChangeRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateMember(address, memberChangeRequest.getContractAddress(), memberChangeRequest.getTargetAddress(), memberChangeRequest.getChangeType());
        ResultView check = memberChangeCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.TARGET_ADDRESS, memberChangeRequest.getTargetAddress());

        if (memberChangeRequest.getChangeType().equals(DaoOperationType.InviteNewPresident.getCode())) {
            additionalInfoMap.put(OperationAdditionalKey.OLD_PRESIDENT, address);
        }

        Map<String, Object> args = SendTxArgsUtil.generate(memberChangeRequest.getChangeType(), additionalInfoMap, address, memberChangeRequest.getContractAddress());
        return daoSendDeletableTransaction.perform(memberChangeRequest.getCallData(), args);
    }

    @PostMapping("/confirm/change/check")
    @ApiOperation(value = "Check confirm member change")
    public ResultView checkConfirmMemberChange(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                               @RequestBody @Validated ConfirmMemberChangeCheckRequest confirmMemberChangeCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateConfirmMember(address, confirmMemberChangeCheckRequest.getContractAddress(), confirmMemberChangeCheckRequest.getChangeType());
        return confirmMemberChangeCheck.check(checkArgs);
    }

    @PostMapping("/confirm/change")
    @ApiOperation(value = "Confirm member change")
    public ResultView confirmMemberChange(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                          @RequestBody @Validated ConfirmMemberChangeRequest confirmMemberChangeRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateConfirmMember(address, confirmMemberChangeRequest.getContractAddress(), confirmMemberChangeRequest.getChangeType());
        ResultView check = confirmMemberChangeCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Map<String, Object> additionalInfoMap = Maps.newHashMap();

        Map<String, Object> args = SendTxArgsUtil.generate(confirmMemberChangeRequest.getChangeType(), additionalInfoMap, address, confirmMemberChangeRequest.getContractAddress());
        return daoSendDeletableTransaction.perform(confirmMemberChangeRequest.getCallData(), args);
    }
}
