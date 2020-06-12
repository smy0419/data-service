package network.asimov.controller.dorg;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.constant.DaoConstants;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.dorg.Member;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.dorg.MemberService;
import network.asimov.mysql.constant.DaoMessage;
import network.asimov.mysql.constant.DaoOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TDaoMessage;
import network.asimov.mysql.database.tables.pojos.TDaoOrganization;
import network.asimov.mysql.service.dorg.DaoMessageService;
import network.asimov.mysql.service.dorg.DaoOperationService;
import network.asimov.mysql.service.dorg.DaoOrganizationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.request.dorg.*;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.TxView;
import network.asimov.response.dorg.OrgInfoView;
import network.asimov.response.dorg.PreparedTx;
import network.asimov.util.CreateContractCodec;
import network.asimov.util.EncodeDecodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-12-09
 */
@CrossOrigin
@RestController("daoOrgController")
@Api(tags = {"dao" })
@RequestMapping(path = "/dao/org", produces = RequestConstants.CONTENT_TYPE_JSON)
public class OrgController {
    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "createOrgCheck")
    private CheckBehavior createOrgCheck;

    @Resource(name = "closeOrgCheck")
    private CheckBehavior closeOrgCheck;

    @Resource(name = "modifyOrgCheck")
    private CheckBehavior modifyOrgCheck;

    @Resource(name = "daoSendDeletableTransaction")
    private SendTransactionBehavior sendDeletableTransaction;

    @Resource(name = "daoMysqlOrganizationService")
    private DaoOrganizationService daoMysqlOrganizationService;

    @Resource(name = "daoMessageService")
    private DaoMessageService daoMessageService;

    @Resource(name = "daoOperationService")
    private DaoOperationService daoOperationService;

    @PostMapping("/list")
    @ApiOperation(value = "List my organization")
    public ResultView<PageView<OrgInfoView>> listOrg(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                     @RequestBody @Validated PurePageQuery purePageQuery) {
        Pair<Long, List<Member>> pair = memberService.listMemberByAddress(address, purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<OrgInfoView> orgInfoViewList = Lists.newArrayList();
        for (Member member : pair.getRight()) {
            Optional<TDaoOrganization> mySqlOrgOptional = daoMysqlOrganizationService.getOrgByContractAddress(member.getContractAddress());
            if (!mySqlOrgOptional.isPresent()) {
                continue;
            }
            TDaoOrganization org = mySqlOrgOptional.get();
            long memberCount = memberService.countMember(org.getContractAddress());

            orgInfoViewList.add(OrgInfoView.builder()
                    .contractAddress(org.getContractAddress())
                    .voteContractAddress(org.getVoteContractAddress())
                    .name(org.getOrgName())
                    .roleType(member.getRole())
                    .joinTime(member.getTime())
                    .status((int) org.getState())
                    .logo(org.getOrgLogo())
                    .memberCount(memberCount)
                    .build());
        }

        PageView pageView = PageView.of(pair.getLeft(), orgInfoViewList);
        return ResultView.ok(pageView);
    }

    @PostMapping("/create/prepare")
    @ApiOperation(value = "Pre-create organization")
    public ResultView<PreparedTx> createPrepare(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                @RequestBody @Validated PrepareCreateOrgRequest prepareCreateOrgRequest) {
        String ctor = EncodeDecodeUtil.encodeConstructor(prepareCreateOrgRequest.getOrgName(), "vote");
        String data = CreateContractCodec.encode(1, "64616f", ctor);
        PreparedTx preparedTx = PreparedTx.builder()
                .to(DaoConstants.CREATE_CONTRACT_ADDR)
                .type(DaoConstants.TX_CREATE)
                .data(data)
                .build();
        return ResultView.ok(preparedTx);
    }

    @PostMapping("/create/check")
    @ApiOperation(value = "Check create organization")
    public ResultView createCheck(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                  @RequestBody @Validated CreateOrgCheckRequest createOrgCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateOrgName(createOrgCheckRequest.getOrgName());
        return createOrgCheck.check(checkArgs);
    }

    @PostMapping("/create")
    @ApiOperation(value = "Create organization")
    public ResultView<TxView> createOrg(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                        @RequestBody @Validated CreateOrgRequest createOrgRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateOrgName(createOrgRequest.getOrgName());
        ResultView check = createOrgCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }

        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.ORG_NAME, createOrgRequest.getOrgName());
        if (StringUtils.isNotBlank(createOrgRequest.getOrgLogo())) {
            additionalInfoMap.put(OperationAdditionalKey.ORG_LOGO, createOrgRequest.getOrgLogo());
        }

        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.CreateOrg.getCode(), additionalInfoMap, address);
        ResultView<TxView> resultView = sendDeletableTransaction.perform(createOrgRequest.getCallData(), args);

        String txHash = resultView.getData().getTxHash();
        TDaoOrganization org = new TDaoOrganization();
        org.setOrgName(createOrgRequest.getOrgName());
        org.setCreatorAddress(address);
        org.setTxHash(txHash);
        org.setState((byte) Organization.Status.Init.ordinal());
        if (StringUtils.isNotBlank(createOrgRequest.getOrgLogo())) {
            org.setOrgLogo(createOrgRequest.getOrgLogo());
        }

        daoMysqlOrganizationService.saveOrganization(org);

        return resultView;
    }

    @PostMapping("/close/check")
    @ApiOperation(value = "Check close organization")
    public ResultView checkClose(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                 @RequestBody @Validated CloseOrgCheckRequest closeOrgCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateContract(address, closeOrgCheckRequest.getContractAddress());
        return closeOrgCheck.check(checkArgs);
    }

    @PostMapping("/close")
    @ApiOperation(value = "Close organization")
    public ResultView<TxView> closeOrg(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                       @RequestBody @Validated CloseOrgRequest closeOrgRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateContract(address, closeOrgRequest.getContractAddress());
        ResultView check = closeOrgCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }
        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.CloseOrg.getCode(), additionalInfoMap, address, closeOrgRequest.getContractAddress());
        return sendDeletableTransaction.perform(closeOrgRequest.getCallData(), args);
    }

    @PostMapping("/update/check")
    @ApiOperation(value = "Check update organization")
    public ResultView checkUpdate(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                  @RequestBody @Validated ModifyOrgCheckRequest modifyOrgCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateOrg(address, modifyOrgCheckRequest.getContractAddress(), modifyOrgCheckRequest.getOrgName(), Strings.EMPTY);
        return modifyOrgCheck.check(checkArgs);
    }

    @PostMapping("/update/name")
    @ApiOperation(value = "Modify organization name")
    public ResultView<TxView> updateOrgName(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                            @RequestBody @Validated ModifyOrgNameRequest modifyOrgNameRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateOrg(address, modifyOrgNameRequest.getContractAddress(), modifyOrgNameRequest.getOrgName(), OperationAdditionalKey.ORG_NAME);
        ResultView check = modifyOrgCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }
        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.ORG_NAME, modifyOrgNameRequest.getOrgName());

        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.ModifyOrgName.getCode(), additionalInfoMap, address, modifyOrgNameRequest.getContractAddress());
        return sendDeletableTransaction.perform(modifyOrgNameRequest.getCallData(), args);
    }

    @PostMapping("/update/logo")
    @ApiOperation(value = "Modify organization logo")
    public ResultView updateOrgLogo(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                    @RequestBody @Validated ModifyOrgLogoRequest modifyOrgLogoRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generateOrg(address, modifyOrgLogoRequest.getContractAddress(), Strings.EMPTY, OperationAdditionalKey.ORG_LOGO);
        ResultView check = modifyOrgCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }
        daoMysqlOrganizationService.updateOrganization(modifyOrgLogoRequest.getContractAddress(), modifyOrgLogoRequest.getOrgLogo());

        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.ORG_LOGO, modifyOrgLogoRequest.getOrgLogo());

        // 记录operation
        Map<String, Object> args = SendTxArgsUtil.generate(DaoOperationType.ModifyOrgLogo.getCode(), additionalInfoMap, address, modifyOrgLogoRequest.getContractAddress());
        daoOperationService.insert(args);

        TDaoMessage message = new TDaoMessage();
        message.setCategory(DaoMessage.Category.ModifyOrgLogo.getCode());
        message.setType(DaoMessage.Type.ReadOnly.ordinal());
        message.setContractAddress(modifyOrgLogoRequest.getContractAddress());
        message.setMessagePosition(DaoMessage.Position.Both.ordinal());
        message.setReceiver(Strings.EMPTY);
        message.setAdditionalInfo(JSON.toJSONString(additionalInfoMap));
        message.setState(DaoMessage.State.Unread.ordinal());

        daoMessageService.saveMessage(message);

        return ResultView.ok();
    }

    @PostMapping("/query")
    @ApiOperation(value = "Get organization information")
    public ResultView<OrgInfoView> getOrgInfo(@RequestBody @Validated ContractAddressQuery contractAddressQuery) {
        Optional<TDaoOrganization> orgOptional = daoMysqlOrganizationService.getOrgByContractAddress(contractAddressQuery.getContractAddress());
        if (!orgOptional.isPresent()) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
        TDaoOrganization org = orgOptional.get();
        long memberCount = memberService.countMember(org.getContractAddress());

        return ResultView.ok(OrgInfoView.builder()
                .name(org.getOrgName())
                .logo(org.getOrgLogo())
                .status((int) org.getState())
                .contractAddress(org.getContractAddress())
                .voteContractAddress(org.getVoteContractAddress())
                .memberCount(memberCount)
                .build());
    }
}
