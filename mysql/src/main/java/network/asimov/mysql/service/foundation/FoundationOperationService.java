package network.asimov.mysql.service.foundation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TFoundationOperation;
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
 * @date 2019-10-14
 */
@Service("foundationOperationService")
public class FoundationOperationService extends BaseService implements OperationService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    public Pair<Long, List<TFoundationOperation>> listFoundationOperationByType(int operationType, Integer index, Integer limit) {
        Condition eqCondition = Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE.equal((byte) operationType);
        return queryByPage(Tables.T_FOUNDATION_OPERATION,
                eqCondition,
                index,
                limit,
                Tables.T_FOUNDATION_OPERATION.CREATE_TIME.desc(),
                TFoundationOperation.class);
    }

    public Pair<Long, List<TFoundationOperation>> listFoundationOperationByAddressAndType(String address, int operationType, Integer index, Integer limit) {
        Condition condition = Tables.T_FOUNDATION_OPERATION.OPERATOR.equal(address).and(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE.equal((byte) operationType));
        return queryByPage(Tables.T_FOUNDATION_OPERATION,
                condition,
                index,
                limit,
                Tables.T_FOUNDATION_OPERATION.CREATE_TIME.desc(),
                TFoundationOperation.class);
    }

    public List<TFoundationOperation> listByOperateType(int operationType) {
        Condition condition = Tables.T_FOUNDATION_OPERATION
                .OPERATION_TYPE.eq((byte) operationType);
        List<TFoundationOperation> list = dSLContext.select()
                .from(Tables.T_FOUNDATION_OPERATION)
                .where(condition)
                .fetchInto(TFoundationOperation.class);
        return list;
    }

    public Map<String, TFoundationOperation> mapTFoundationOperationByTxHash(List<String> txHash) {
        Map<String, TFoundationOperation> map = Maps.newHashMap();
        if (txHash == null) {
            return map;
        }

        for (String hash : txHash) {
            Optional<TFoundationOperation> optional = getTFoundationOperationByTxHash(hash);
            if (optional.isPresent()) {
                TFoundationOperation result = optional.get();
                map.put(hash, result);
            }
        }
        return map;
    }

    public Optional<TFoundationOperation> getTFoundationOperationByTxHash(String txHash) {
        Condition condition = Tables.T_FOUNDATION_OPERATION.TX_HASH.eq(txHash);
        TFoundationOperation result = dSLContext.select()
                .from(Tables.T_FOUNDATION_OPERATION)
                .where(condition)
                .fetchOneInto(TFoundationOperation.class);

        return Optional.ofNullable(result);
    }

    /**
     * Check whether the user's voting information exists
     *
     * @param proposalId    proposal ID
     * @param address       User Address
     * @param operationType Operation Type
     * @return exist or not
     */
    public boolean applyExist(long proposalId, String address, int operationType) {
        Condition condition = Tables.T_FOUNDATION_OPERATION
                .OPERATOR.eq(address)
                .and(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE.eq((byte) operationType));

        List<TFoundationOperation> operationList = dSLContext.select()
                .from(Tables.T_FOUNDATION_OPERATION)
                .where(condition)
                .fetchInto(TFoundationOperation.class);

        if (operationList.isEmpty()) {
            return false;
        }
        for (TFoundationOperation operation : operationList) {
            Long proposalIdInDb = JSON.parseObject(operation.getAdditionalInfo()).getLong(OperationAdditionalKey.PROPOSAL_ID);
            if (proposalIdInDb != null && proposalId == proposalIdInDb) {
                if ((int) operation.getTxStatus() == TxStatus.Pending.ordinal() || (int) operation.getTxStatus() == TxStatus.Success.ordinal()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void modifyTxHash(long id, String txHash) {
        dSLContext.update(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.TX_HASH, txHash)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, TimeUtil.currentSeconds())
                .where(Tables.T_FOUNDATION_OPERATION.ID.eq(id))
                .execute();
    }

    @Override
    public void drop(long id) {
        dSLContext.delete(Tables.T_FOUNDATION_OPERATION)
                .where(Tables.T_FOUNDATION_OPERATION.ID.eq(id))
                .execute();
    }

    @Override
    public long insert(Map<String, Object> args) {
        int operationType = (int) args.get("operationType");
        Map<String, Object> additionalInfoMap = (Map<String, Object>) args.get("additionalInfoMap");
        String operator = (String) args.get("operator");

        long id = globalIdService.nextId();
        long now = TimeUtil.currentSeconds();
        JSONObject additionalInfoJson = new JSONObject();
        additionalInfoMap.forEach((k, v) -> additionalInfoJson.put(k, v));

        dSLContext.insertInto(Tables.T_FOUNDATION_OPERATION)
                .set(Tables.T_FOUNDATION_OPERATION.ID, id)
                .set(Tables.T_FOUNDATION_OPERATION.OPERATION_TYPE, (byte) operationType)
                .set(Tables.T_FOUNDATION_OPERATION.ADDITIONAL_INFO, additionalInfoJson.toJSONString())
                .set(Tables.T_FOUNDATION_OPERATION.OPERATOR, operator)
                .set(Tables.T_FOUNDATION_OPERATION.CREATE_TIME, now)
                .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, now)
                .execute();

        return id;
    }

    public List<String> listUnknownTransactions() {
        Condition condition = Tables.T_FOUNDATION_OPERATION.TX_STATUS.eq((byte) TxStatus.Pending.ordinal());
        List<String> list = dSLContext.select(Tables.T_FOUNDATION_OPERATION.TX_HASH)
                .from(Tables.T_FOUNDATION_OPERATION)
                .where(condition)
                .fetchInto(String.class);
        return list;
    }

    public void updateTxStatusByTxHashList(List<String> txHashList) {
        if (txHashList != null) {
            txHashList.forEach(v -> dSLContext.update(Tables.T_FOUNDATION_OPERATION)
                    .set(Tables.T_FOUNDATION_OPERATION.TX_STATUS, (byte) TxStatus.ContractRevert.ordinal())
                    .set(Tables.T_FOUNDATION_OPERATION.UPDATE_TIME, TimeUtil.currentSeconds())
                    .where(Tables.T_FOUNDATION_OPERATION.TX_HASH.eq(v))
                    .execute());
        }
    }

}
