package network.asimov.controller.dorg;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.error.ErrorCode;
import network.asimov.mongodb.entity.ascan.Asset;
import network.asimov.mongodb.entity.dorg.Organization;
import network.asimov.mongodb.service.ascan.AssetService;
import network.asimov.mongodb.service.dorg.MemberService;
import network.asimov.mongodb.service.dorg.OrganizationService;
import network.asimov.mysql.database.tables.pojos.TDaoAccount;
import network.asimov.mysql.database.tables.pojos.TDaoMessage;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.mysql.service.dorg.DaoMessageService;
import network.asimov.request.RequestConstants;
import network.asimov.request.common.PurePageQuery;
import network.asimov.request.dorg.MessagePageQuery;
import network.asimov.request.dorg.MessageRequest;
import network.asimov.response.PageView;
import network.asimov.response.ResultView;
import network.asimov.response.dorg.MessageInfoView;
import org.apache.commons.lang3.StringUtils;
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
 * @date 2019-12-17
 */
@CrossOrigin
@RestController("daoMessageController")
@Api(tags = {"dao"})
@RequestMapping(path = "/dao/message", produces = RequestConstants.CONTENT_TYPE_JSON)
public class MessageController {
    @Resource(name = "daoMessageService")
    private DaoMessageService daoMessageService;

    @Resource(name = "daoMemberService")
    private MemberService memberService;

    @Resource(name = "daoOrganizationService")
    private OrganizationService organizationService;

    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @Resource(name = "assetService")
    private AssetService assetService;

    @PostMapping("/list/mine")
    @ApiOperation(value = "Query my messages by page")
    public ResultView<PageView<MessageInfoView>> listMyMessage(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                               @RequestBody @Validated PurePageQuery purePageQuery) {
        List<String> orgList = memberService.listMyOrg(address);
        Pair<Long, List<TDaoMessage>> pair = daoMessageService.listMyMessage(address, orgList, purePageQuery.getPage().getIndex(), purePageQuery.getPage().getLimit());
        List<MessageInfoView> messageInfoViewList = Lists.newArrayList();

        List<String> messageOrgList = pair.getRight().stream().distinct().map(TDaoMessage::getContractAddress).collect(Collectors.toList());
        Map<String, Organization> orgMap = organizationService.mapOrganization(messageOrgList);

        for (TDaoMessage message : pair.getRight()) {
            Map<String, Object> additionalInfoMap = JSON.parseObject(message.getAdditionalInfo());
            Organization organization = orgMap.get(message.getContractAddress());

            messageInfoViewList.add(MessageInfoView.builder()
                    .id(String.valueOf(message.getId()))
                    .category(message.getCategory())
                    .type(message.getType())
                    .messagePosition(message.getMessagePosition())
                    .orgName(organization.getOrgName())
                    .contractAddress(organization.getContractAddress())
                    .voteContractAddress(organization.getVoteContractAddress())
                    .additionalInfo(additionalInfoMap)
                    .state(message.getState())
                    .createTime(message.getCreateTime())
                    .build());
        }

        PageView pageView = PageView.of(pair.getLeft(), messageInfoViewList);
        return ResultView.ok(pageView);
    }

    @PostMapping("/list/org")
    @ApiOperation(value = "Query organization messages by page")
    public ResultView<PageView<MessageInfoView>> listOrgMessage(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                                                @RequestBody @Validated MessagePageQuery messagePageQuery) {
        Pair<Long, List<TDaoMessage>> pair = daoMessageService.listMessageByContractAddress(messagePageQuery.getContractAddress(), messagePageQuery.getPage().getIndex(), messagePageQuery.getPage().getLimit());
        List<MessageInfoView> messageInfoViewList = Lists.newArrayList();
        for (TDaoMessage message : pair.getRight()) {
            Map<String, Object> additionalInfoMap = JSON.parseObject(message.getAdditionalInfo());
            String targetAddress = (String) additionalInfoMap.get("target_address");
            if (StringUtils.isNotBlank(targetAddress)) {
                TDaoAccount account = daoAccountService.findByAddress(targetAddress);
                if (account != null) {
                    additionalInfoMap.put("target_person_name", account.getNickName());
                }
            }

            String oldPresident = (String) additionalInfoMap.get("old_president");
            if (StringUtils.isNotBlank(oldPresident)) {
                TDaoAccount account = daoAccountService.findByAddress(oldPresident);
                if (account != null) {
                    additionalInfoMap.put("old_president_name", account.getNickName());
                }
            }

            String newPresident = (String) additionalInfoMap.get("new_president");
            if (StringUtils.isNotBlank(newPresident)) {
                TDaoAccount account = daoAccountService.findByAddress(newPresident);
                if (account != null) {
                    additionalInfoMap.put("new_president_name", account.getNickName());
                }
            }

            String asset = (String) additionalInfoMap.get("asset");
            if (StringUtils.isNotBlank(asset)) {
                Optional<Asset> assetOptional = assetService.getAsset(asset);
                additionalInfoMap.put("asset_name", assetOptional.get().getName());
            }

            messageInfoViewList.add(MessageInfoView.builder()
                    .id(String.valueOf(message.getId()))
                    .category(message.getCategory())
                    .type(message.getType())
                    .messagePosition(message.getMessagePosition())
                    .contractAddress(message.getContractAddress())
                    .additionalInfo(additionalInfoMap)
                    .state(message.getState())
                    .createTime(message.getCreateTime())
                    .build());
        }
        PageView pageView = PageView.of(pair.getLeft(), messageInfoViewList);
        return ResultView.ok(pageView);
    }

    @PostMapping("/update")
    @ApiOperation(value = "Modify message status")
    public ResultView updateMessage(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                    @RequestBody @Validated MessageRequest messageRequest) {
        int row = daoMessageService.updateMessage(messageRequest.getMessageId(), messageRequest.getState());
        if (row > 0) {
            return ResultView.ok();
        } else {
            return ResultView.error(ErrorCode.READ_MESSAGE_ERROR);
        }
    }
}
