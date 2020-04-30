package network.asimov.controller.foundation;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.behavior.check.CheckArgsUtil;
import network.asimov.behavior.check.CheckBehavior;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.behavior.sendtx.SendTxArgsUtil;
import network.asimov.mongodb.pojo.VoteDTO;
import network.asimov.mongodb.service.foundation.VoteService;
import network.asimov.mysql.constant.FoundationOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.tables.pojos.TFoundationOperation;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.mysql.service.foundation.FoundationOperationService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.request.common.VoteCheckRequest;
import network.asimov.request.common.VoteRequest;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.common.PersonView;
import network.asimov.response.common.TxView;
import network.asimov.response.foundation.VoteView;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author sunmengyuan
 * @date 2019-09-23
 */
@CrossOrigin
@RestController("foundationVoteController")
@Api(tags = "foundation")
@RequestMapping(path = "/foundation", produces = RequestConstants.CONTENT_TYPE_JSON, consumes = RequestConstants.CONTENT_TYPE_JSON)
public class VoteController {
    @Resource(name = "foundationVoteService")
    private VoteService voteService;

    @Resource(name = "foundationOperationService")
    private FoundationOperationService foundationOperationService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "foundationSendDeletableTransaction")
    private SendTransactionBehavior foundationSendDeletableTransaction;

    @Resource(name = "foundationVoteCheck")
    private CheckBehavior foundationVoteCheck;

    @ApiOperation(value = "Query my voted proposal by page")
    @PostMapping(path = "/vote/mine")
    public ResultView<PageView<VoteView>> listMyVote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address, @RequestBody @Validated PurePageQuery purePageQuery) {
        List<VoteView> voteViewList = Lists.newArrayList();
        Pair<Long, List<TFoundationOperation>> pair = foundationOperationService.listFoundationOperationByAddressAndType(address, FoundationOperationType.Vote.getCode(), purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());

        List<VoteDTO> voteDTOList = voteService.listVoteDTOByTxHash(pair.getRight().stream().map(TFoundationOperation::getTxHash).distinct().collect(Collectors.toList()));
        Map<String, VoteDTO> voteMap = voteDTOList.stream().collect(Collectors.toMap(VoteDTO::getTxHash, voteDTO -> voteDTO));
        List<String> addressList = voteDTOList.stream().map(VoteDTO::getAddress).distinct().collect(Collectors.toList());

        List<Account> list = daoAccountService.listAll(addressList);
        Map<String, Account> userMap = list.stream().collect(Collectors.toMap(Account::getAddress, account -> account));

        List<String> txHashList = voteDTOList.stream().map(VoteDTO::getProposalTxHash).distinct().collect(Collectors.toList());
        Map<String, TFoundationOperation> operationMap = foundationOperationService.mapTFoundationOperationByTxHash(txHashList);

        for (TFoundationOperation operation : pair.getRight()) {
            VoteDTO voteDTO = voteMap.get(operation.getTxHash());

            VoteView voteView = VoteView.builder()
                    .txStatus(operation.getTxStatus())
                    .decision(JSON.parseObject(operation.getAdditionalInfo()).getBoolean(OperationAdditionalKey.DECISION))
                    .build();
            if (voteDTO != null) {
                TFoundationOperation proposalOperation = operationMap.get(voteDTO.getProposalTxHash());
                Account account = userMap.get(voteDTO.getAddress());

                voteView.setProposalId(voteDTO.getProposalId());
                voteView.setType(voteDTO.getType());
                voteView.setTime(operation.getCreateTime());
                if (proposalOperation != null) {
                    voteView.setComment(JSON.parseObject(proposalOperation.getAdditionalInfo()).getString(OperationAdditionalKey.COMMENT));
                } else {
                    voteView.setComment("");
                }
                voteView.setProposalStatus(voteDTO.getStatus());
                voteView.setProposer(PersonView.builder()
                        .name(account != null ? account.getName() : "")
                        .build());
            } else {
                voteView.setProposalId(-1);
                voteView.setType(-1);
                voteView.setComment("");
                voteView.setProposalStatus(-1);
                voteView.setProposer(PersonView.builder().name("").build());
            }
            voteViewList.add(voteView);
        }

        PageView<VoteView> pageView = PageView.of(pair.getLeft(), voteViewList);
        return ResultView.ok(pageView);
    }

    @ApiOperation(value = "Check vote")
    @PostMapping(path = "/vote/check", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView checkVote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                @RequestBody @Validated VoteCheckRequest voteCheckRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generate(address, voteCheckRequest.getProposalId());
        return foundationVoteCheck.check(checkArgs);
    }

    @ApiOperation(value = "Vote", notes = "call /foundation/contract returns abi => vote(uint proposalId, bool decision)")
    @PostMapping(path = "/vote", consumes = RequestConstants.CONTENT_TYPE_JSON)
    public ResultView<TxView> vote(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                   @RequestBody @Validated VoteRequest voteRequest) {
        Map<String, Object> checkArgs = CheckArgsUtil.generate(address, voteRequest.getProposalId());
        ResultView check = foundationVoteCheck.check(checkArgs);
        if (!check.success()) {
            return check;
        }
        Map<String, Object> additionalInfoMap = Maps.newHashMap();
        additionalInfoMap.put(OperationAdditionalKey.PROPOSAL_ID, voteRequest.getProposalId());
        additionalInfoMap.put(OperationAdditionalKey.DECISION, voteRequest.getDecision());

        Map<String, Object> args = SendTxArgsUtil.generate(FoundationOperationType.Vote.getCode(), additionalInfoMap, address);
        return foundationSendDeletableTransaction.perform(voteRequest.getCallData(), args);
    }
}
