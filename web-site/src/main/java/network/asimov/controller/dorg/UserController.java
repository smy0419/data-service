package network.asimov.controller.dorg;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import network.asimov.error.ErrorCode;
import network.asimov.mysql.database.tables.pojos.TDaoAccount;
import network.asimov.mysql.service.dorg.DaoAccountService;
import network.asimov.request.RequestConstants;
import network.asimov.request.dorg.AccountRequest;
import network.asimov.response.ResultView;
import network.asimov.response.dorg.AccountView;
import network.asimov.util.TimeUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;

/**
 * @author sunmengyuan
 * @date 2019-12-11
 */
@CrossOrigin
@RestController("daoUserController")
@Api(tags = "dao")
@RequestMapping(path = "/dao/org/user", produces = RequestConstants.CONTENT_TYPE_JSON)
public class UserController {
    @Resource(name = "daoAccountService")
    private DaoAccountService daoAccountService;

    @ApiOperation(value = "Get account information via address")
    @PostMapping("/query")
    public ResultView<AccountView> getAccount(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address) {
        TDaoAccount tAccount = daoAccountService.findOrAdd(address);
        AccountView accountView = AccountView.builder()
                .address(address)
                .name(tAccount != null ? tAccount.getNickName() : "")
                .icon(tAccount != null ? tAccount.getAvatar() : "")
                .build();
        return ResultView.ok(accountView);
    }

    @ApiOperation(value = "Update account")
    @PostMapping("/update")
    public ResultView updateAccount(@ApiIgnore @RequestAttribute(RequestConstants.ADDRESS) String address,
                                    @RequestBody @Validated AccountRequest accountRequest) {
        TDaoAccount account = new TDaoAccount();
        account.setAddress(address);
        account.setAvatar(accountRequest.getIcon());
        account.setNickName(accountRequest.getName());
        account.setUpdateTime(TimeUtil.currentSeconds());
        int rowNum = daoAccountService.updateAccount(account);
        if (rowNum == 0) {
            return ResultView.error(ErrorCode.DATA_NOT_EXISTS);
        }
        return ResultView.ok();
    }
}
