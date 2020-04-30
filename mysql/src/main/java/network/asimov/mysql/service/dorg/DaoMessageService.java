package network.asimov.mysql.service.dorg;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import network.asimov.mysql.constant.DaoMessage;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoMessage;
import network.asimov.mysql.service.BaseService;
import network.asimov.mysql.service.GlobalIdService;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2020-01-15
 */
@Service("daoMessageService")
public class DaoMessageService extends BaseService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    public void saveMessage(TDaoMessage obj) {
        long id = globalIdService.nextId();
        long now = TimeUtil.currentSeconds();
        dSLContext.insertInto(Tables.T_DAO_MESSAGE)
                .set(Tables.T_DAO_MESSAGE.ID, id)
                .set(Tables.T_DAO_MESSAGE.CATEGORY, obj.getCategory())
                .set(Tables.T_DAO_MESSAGE.TYPE, obj.getType())
                .set(Tables.T_DAO_MESSAGE.MESSAGE_POSITION, obj.getMessagePosition())
                .set(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS, obj.getContractAddress())
                .set(Tables.T_DAO_MESSAGE.RECEIVER, obj.getReceiver())
                .set(Tables.T_DAO_MESSAGE.ADDITIONAL_INFO, obj.getAdditionalInfo())
                .set(Tables.T_DAO_MESSAGE.STATE, obj.getState())
                .set(Tables.T_DAO_MESSAGE.CREATE_TIME, now)
                .set(Tables.T_DAO_MESSAGE.UPDATE_TIME, now)
                .execute();
    }

    // select * from t_dao_message where message_position <> 1 and (receiver = ? or (receiver = '' and contract_address in (?,?)));
    public Pair<Long, List<TDaoMessage>> listMyMessage(String address, List<String> orgList, Integer index, Integer limit) {
        if (orgList == null) {
            return Pair.of(0L, Lists.newArrayList());
        }
        Condition condition;
        if (orgList.isEmpty()) {
            condition = Tables.T_DAO_MESSAGE.RECEIVER.equal(address).and(Tables.T_DAO_MESSAGE.MESSAGE_POSITION.ne(DaoMessage.Position.Dao.ordinal()));
        } else {
            condition = Tables.T_DAO_MESSAGE.MESSAGE_POSITION.ne(DaoMessage.Position.Dao.ordinal())
                    .and(Tables.T_DAO_MESSAGE.RECEIVER.equal(address)
                            .or(Tables.T_DAO_MESSAGE.RECEIVER.equal(Strings.EMPTY).and(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS.in(orgList)))
                    );
        }

        return queryByPage(Tables.T_DAO_MESSAGE,
                condition,
                index,
                limit,
                Tables.T_DAO_MESSAGE.CREATE_TIME.desc(),
                TDaoMessage.class);
    }

    // select * from t_dao_message where contract_address = ? and message_position <> web
    public Pair<Long, List<TDaoMessage>> listMessageByContractAddress(String contractAddress, Integer index, Integer limit) {
        Condition condition = Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS.eq(contractAddress)
                .and(Tables.T_DAO_MESSAGE.MESSAGE_POSITION.ne(DaoMessage.Position.Web.ordinal()));

        return queryByPage(Tables.T_DAO_MESSAGE,
                condition,
                index,
                limit,
                Tables.T_DAO_MESSAGE.CREATE_TIME.desc(),
                TDaoMessage.class);
    }

    /**
     * 根据消息ID更新消息状态
     *
     * @param messageId 消息ID
     * @param state     更新后的状态
     * @return 更新的记录数
     */
    public int updateMessage(Long messageId, Integer state) {
        return dSLContext.update(Tables.T_DAO_MESSAGE)
                .set(Tables.T_DAO_MESSAGE.STATE, state)
                .where(Tables.T_DAO_MESSAGE.ID.eq(messageId)).execute();
    }

    /**
     * 查询消息表中关于投票的未读消息
     *
     * @param receiver        消息接收者
     * @param contractAddress 组织合约地址
     * @param proposalId      投票对应的提议Id
     * @return
     */
    public Optional<TDaoMessage> getVoteMessage(String receiver, String contractAddress, Long proposalId) {
        Condition condition = Tables.T_DAO_MESSAGE.RECEIVER.eq(receiver)
                .and(Tables.T_DAO_MESSAGE.CONTRACT_ADDRESS.eq(contractAddress))
                .and(Tables.T_DAO_MESSAGE.STATE.eq(DaoMessage.State.Unread.ordinal()))
                .and(Tables.T_DAO_MESSAGE.MESSAGE_POSITION.eq(DaoMessage.Position.Web.ordinal()));

        List<TDaoMessage> result = dSLContext.select()
                .from(Tables.T_DAO_MESSAGE)
                .where(condition)
                .fetchInto(TDaoMessage.class);
        for (TDaoMessage message : result) {
            Map<String, Object> additionalInfoMap = JSON.parseObject(message.getAdditionalInfo());
            if (additionalInfoMap.containsKey(OperationAdditionalKey.PROPOSAL_ID)) {
                int proposalIdInDb = (int) additionalInfoMap.get(OperationAdditionalKey.PROPOSAL_ID);
                if (proposalId.intValue() == proposalIdInDb) {
                    return Optional.ofNullable(message);
                }
            }
        }
        return Optional.empty();
    }

}
