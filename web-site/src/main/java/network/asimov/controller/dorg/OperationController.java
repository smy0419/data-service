package network.asimov.controller.dorg;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.dorg.Member;
import network.asimov.mongodb.entity.dorg.Proposal;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mongodb.service.dorg.MemberService;
import network.asimov.mongodb.service.dorg.ProposalService;
import network.asimov.mysql.constant.DaoOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.database.tables.pojos.TDaoAccount;
import network.asimov.mysql.database.tables.pojos.TDaoOperation;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.mysql.service.dorg.DaoOperationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.TxHashRequest;
import network.asimov.request.dorg.ContractPageQuery;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.common.TxStatusView;
import network.asimov.response.dorg.OperationRecordView;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-10-29
 */
@CrossOrigin
@RestController("daoOperationController")
@Api(tags = "dao")
@RequestMapping(path = "/dao", produces = RequestConstants.CONTENT_TYPE_JSON)
public class OperationController {
    @Resource(name = "daoOperationService")
    private DaoOperationService daoOperationService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "daoProposalService")
    private ProposalService daoProposalService;

    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "assetService")
    private AssetService assetService;

    @ApiOperation(value = "Get transaction status via hash")
    @PostMapping(path = "/transaction/status")
    public ResultView<TxStatusView> getTxStatusByHash(@RequestBody @Validated TxHashRequest txHashRequest) {
        Optional<TDaoOperation> operation = daoOperationService.getOperationByTxHash(txHashRequest.getTxHash());
        return operation.map(TDaoOperation -> ResultView.ok(TxStatusView.builder().txStatus(TDaoOperation.getTxStatus()).build()))
                .orElseGet(() -> ResultView.error(ErrorCode.DATA_NOT_EXISTS));
    }

    @ApiOperation(value = "List operation record")
    @PostMapping(path = "/operation/record/list")
    public ResultView<PageView<OperationRecordView>> listOperationRecord(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                                         @RequestBody @Validated ContractPageQuery contractPageQuery) {
        List<OperationRecordView> viewList = Lists.newArrayList();
        Pair<Long, List<TDaoOperation>> pair = daoOperationService.listOperationByOrg(contractPageQuery.getContractAddress(), contractPageQuery.getPage().getIndex(), contractPageQuery.getPage().getLimit());
        for (TDaoOperation operation : pair.getRight()) {
            Map<String, Object> additionalInfoMap = JSON.parseObject(operation.getAdditionalInfo());

            OperationRecordView operationRecordView = OperationRecordView.builder()
                    .operationId(String.valueOf(operation.getId()))
                    .operationType(Integer.valueOf(operation.getOperationType()))
                    .txStatus(Integer.valueOf(operation.getTxStatus()))
                    .createTime(operation.getCreateTime())
                    .build();
            if (operation.getOperationType() == DaoOperationType.IssueAsset.getCode()) {
                if (operation.getTxStatus() == TxStatus.Success.ordinal()) {
                    Optional<Proposal> proposalOptional = daoProposalService.getProposalByHash(operation.getTxHash());
                    if (!proposalOptional.isPresent()) {
                        return ResultView.error(ErrorCode.DATA_ERROR);
                    }

                    operationRecordView.setProposalStatus(proposalOptional.get().getStatus());
                    additionalInfoMap.put(OperationAdditionalKey.PROPOSAL_ID, proposalOptional.get().getProposalId());
                    if (proposalOptional.get().getStatus() == Proposal.Status.OnGoing.ordinal() && proposalOptional.get().getEndTime() < TimeUtil.currentSeconds()) {
                        operationRecordView.setProposalStatus(Proposal.Status.Expired.ordinal());
                    }
                }
            }

            String asset = (String) additionalInfoMap.get("asset");
            if (StringUtils.isNotBlank(asset)) {
                Optional<Asset> assetOptional = assetService.getAsset(asset);
                if (assetOptional.isPresent()) {
                    additionalInfoMap.put("asset_name", assetOptional.get().getName());
                }
            }

            TDaoAccount account = daoAccountService.findByAddress(operation.getOperator());
            operationRecordView.setAdditionalInfo(additionalInfoMap);
            operationRecordView.setProposer(PersonView.builder()
                    .name(account != null ? account.getNickName() : "")
                    .address(operation.getOperator())
                    .icon(account != null ? account.getAvatar() : "")
                    .build());
            Optional<Member> memberOptional = memberService.getMemberByAddress(operation.getOperator(), operation.getContractAddress());
            operationRecordView.setProposerRole(memberOptional.get().getRole());

            viewList.add(operationRecordView);
        }

        PageView pageView = PageView.of(pair.getLeft(), viewList);
        return ResultView.ok(pageView);
    }
}
