package network.asimov.mysql.service.dorg;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import network.asimov.mysql.constant.DaoOperationType;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TDaoOperation;
import network.asimov.mysql.service.BaseService;
import network.asimov.mysql.service.GlobalIdService;
import network.asimov.mysql.service.OperationService;
import network.asimov.util.TimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sunmengyuan
 * @date 2019-12-10
 */
@Service("daoOperationService")
public class DaoOperationService extends BaseService implements OperationService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    @Override
    public void modifyTxHash(long id, String txHash) {
        dSLContext.update(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.TX_HASH, txHash)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, TimeUtil.currentSeconds())
                .where(Tables.T_DAO_OPERATION.ID.eq(id))
                .execute();
    }

    @Override
    public void drop(long id) {
        dSLContext.delete(Tables.T_DAO_OPERATION)
                .where(Tables.T_DAO_OPERATION.ID.eq(id))
                .execute();
    }

    @Override
    public long insert(Map<String, Object> args) {
        int operationType = (int) args.get("operationType");
        Map<String, Object> additionalInfoMap = (Map<String, Object>) args.get("additionalInfoMap");
        String operator = (String) args.get("operator");
        String contractAddress = (String) args.get("contractAddress");
        int txStatus = TxStatus.Pending.ordinal();
        if (operationType == DaoOperationType.ModifyOrgLogo.getCode()) {
            txStatus = TxStatus.NoChain.ordinal();
        }

        long id = globalIdService.nextId();
        long now = TimeUtil.currentSeconds();
        JSONObject additionalInfoJson = new JSONObject();
        additionalInfoMap.forEach((k, v) -> additionalInfoJson.put(k, v));

        dSLContext.insertInto(Tables.T_DAO_OPERATION)
                .set(Tables.T_DAO_OPERATION.ID, id)
                .set(Tables.T_DAO_OPERATION.CONTRACT_ADDRESS, contractAddress)
                .set(Tables.T_DAO_OPERATION.OPERATION_TYPE, (byte) operationType)
                .set(Tables.T_DAO_OPERATION.ADDITIONAL_INFO, additionalInfoJson.toJSONString())
                .set(Tables.T_DAO_OPERATION.OPERATOR, operator)
                .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) txStatus)
                .set(Tables.T_DAO_OPERATION.CREATE_TIME, now)
                .set(Tables.T_DAO_OPERATION.UPDATE_TIME, now)
                .execute();

        return id;
    }

    public Pair<Long, List<TDaoOperation>> listOperationByOrg(String contractAddress, Integer index, Integer limit) {
        // query organization action records, except status in [8, 10, 11]
        List<Integer> typeList = Lists.newArrayList(DaoOperationType.ConfirmPresident.getCode(), DaoOperationType.JoinNewMember.getCode(), DaoOperationType.Vote.getCode());
        Condition eqCondition = Tables.T_DAO_OPERATION.CONTRACT_ADDRESS.equal(contractAddress).and(Tables.T_DAO_OPERATION.OPERATION_TYPE.notIn(typeList));
        return queryByPage(Tables.T_DAO_OPERATION,
                eqCondition,
                index,
                limit,
                Tables.T_DAO_OPERATION.CREATE_TIME.desc(),
                TDaoOperation.class);
    }

    public Optional<TDaoOperation> getOperationByTxHash(String txHash) {
        Condition condition = Tables.T_DAO_OPERATION.TX_HASH.eq(txHash);
        TDaoOperation result = dSLContext.select()
                .from(Tables.T_DAO_OPERATION)
                .where(condition)
                .fetchOneInto(TDaoOperation.class);

        return Optional.ofNullable(result);
    }

    public List<String> listUnknownTransactions() {
        Condition condition = Tables.T_DAO_OPERATION.TX_STATUS.eq((byte) TxStatus.Pending.ordinal());
        List<String> list = dSLContext.select(Tables.T_DAO_OPERATION.TX_HASH)
                .from(Tables.T_DAO_OPERATION)
                .where(condition)
                .fetchInto(String.class);
        return list;
    }

    public void updateTxStatusByTxHashList(List<String> txHashList) {
        if (txHashList != null) {
            txHashList.forEach(v -> dSLContext.update(Tables.T_DAO_OPERATION)
                    .set(Tables.T_DAO_OPERATION.TX_STATUS, (byte) TxStatus.ContractRevert.ordinal())
                    .set(Tables.T_DAO_OPERATION.UPDATE_TIME, TimeUtil.currentSeconds())
                    .where(Tables.T_DAO_OPERATION.TX_HASH.eq(v))
                    .execute());
        }
    }
}
