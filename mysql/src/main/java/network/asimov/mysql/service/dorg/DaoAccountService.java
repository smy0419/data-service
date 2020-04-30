package network.asimov.mysql.service.dorg;

import com.google.common.collect.Lists;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoAccount;
import network.asimov.mysql.pojo.Account;
import network.asimov.mysql.service.BaseService;
import network.asimov.mysql.service.GlobalIdService;
import network.asimov.util.TimeUtil;
import org.jooq.Condition;
import org.jooq.tools.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * @author zhangjing
 * @date 2019-11-08
 */
@Service("daoAccountService")
public class DaoAccountService extends BaseService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    /**
     * Get account via address
     * Add one if not exist
     *
     * @param address Address
     * @return TDaoAccount
     */
    public TDaoAccount findOrAdd(String address) {
        if (address == null) {
            throw BusinessException.builder().errorCode(ErrorCode.PARAMETER_INVALID).message("invalid parameter").build();
        }
        Condition condition = Tables.T_DAO_ACCOUNT.ADDRESS.eq(address);
        TDaoAccount result = dSLContext.select()
                .from(Tables.T_DAO_ACCOUNT)
                .where(condition)
                .fetchOneInto(TDaoAccount.class);
        if (result == null) {
            long id = globalIdService.nextId();
            long now = TimeUtil.currentSeconds();
            String nickName = randomName();

            dSLContext.insertInto(Tables.T_DAO_ACCOUNT)
                    .set(Tables.T_DAO_ACCOUNT.ID, id)
                    .set(Tables.T_DAO_ACCOUNT.ADDRESS, address)
                    .set(Tables.T_DAO_ACCOUNT.NICK_NAME, nickName)
                    .set(Tables.T_DAO_ACCOUNT.CREATE_TIME, now)
                    .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, now)
                    .execute();

            result = new TDaoAccount();
            result.setId(id);
            result.setAddress(address);
            result.setAvatar(StringUtils.EMPTY);
            result.setNickName(nickName);
            result.setCreateTime(now);
            result.setUpdateTime(now);
        }

        return result;
    }

    public TDaoAccount findByAddress(String address) {
        if (address == null) {
            throw BusinessException.builder().errorCode(ErrorCode.PARAMETER_INVALID).message("invalid parameter").build();
        }
        Condition condition = Tables.T_DAO_ACCOUNT.ADDRESS.eq(address);
        TDaoAccount result = dSLContext.select()
                .from(Tables.T_DAO_ACCOUNT)
                .where(condition)
                .fetchOneInto(TDaoAccount.class);
        return result;
    }

    /**
     * Get random nick name
     * yyyyMMdd + six random numbers
     *
     * @return 随机昵称
     */
    private String randomName() {
        LocalDate date = LocalDate.now();
        String left = date.format(DateTimeFormatter.BASIC_ISO_DATE);
        String right = UUID.randomUUID().toString();
        return left.substring(2) + right.substring(0, 6);
    }

    public int updateAccount(TDaoAccount account) {
        return dSLContext.update(Tables.T_DAO_ACCOUNT)
                .set(Tables.T_DAO_ACCOUNT.NICK_NAME, account.getNickName())
                .set(Tables.T_DAO_ACCOUNT.AVATAR, account.getAvatar())
                .set(Tables.T_DAO_ACCOUNT.UPDATE_TIME, account.getUpdateTime())
                .where(Tables.T_DAO_ACCOUNT.ADDRESS.eq(account.getAddress()))
                .execute();
    }

    public List<Account> listAll(List<String> addressList) {
        if (addressList == null) {
            return Lists.newArrayList();
        }
        List<Account> accountList = Lists.newArrayList();
        Condition condition = Tables.T_DAO_ACCOUNT.ADDRESS.in(addressList);

        List<TDaoAccount> records = dSLContext.select()
                .from(Tables.T_DAO_ACCOUNT)
                .where(condition)
                .fetchInto(TDaoAccount.class);
        for (TDaoAccount record : records) {
            accountList.add(Account.builder()
                    .id(record.getId())
                    .address(record.getAddress())
                    .avatar(record.getAvatar())
                    .name(record.getNickName())
                    .createTime(record.getCreateTime())
                    .modifyTime(record.getUpdateTime())
                    .build());
        }
        return accountList;
    }
}
