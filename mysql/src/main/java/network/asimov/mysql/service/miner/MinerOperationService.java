package network.asimov.mysql.service.miner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import network.asimov.mysql.constant.MinerOperationType;
import network.asimov.mysql.constant.OperationAdditionalKey;
import network.asimov.mysql.constant.TxStatus;
import network.asimov.mysql.database.Tables;
import network.asimov.mysql.database.tables.pojos.TMinerOperation;
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
 * @date 2019-09-26
 */

@Service("minerOperationService")
public class MinerOperationService extends BaseService implements OperationService {
    @Resource(name = "globalIdService")
    private GlobalIdService globalIdService;

    public Pair<Long, List<TMinerOperation>> listTMinerOperationByType(int operationType, Integer index, Integer limit) {
        Condition eqCondition = Tables.T_MINER_OPERATION.OPERATION_TYPE.equal((byte) operationType);
        return queryByPage(Tables.T_MINER_OPERATION,
                eqCondition,
                index,
                limit,
                Tables.T_MINER_OPERATION.CREATE_TIME.desc(),
                TMinerOperation.class);
    }

    public Pair<Long, List<TMinerOperation>> listTMinerOperationByAddressAndType(String address, int operationType, Integer index, Integer limit) {
        Condition condition = Tables.T_MINER_OPERATION.OPERATOR.equal(address).and(Tables.T_MINER_OPERATION.OPERATION_TYPE.equal((byte) operationType));
        return queryByPage(Tables.T_MINER_OPERATION,
                condition,
                index,
                limit,
                Tables.T_MINER_OPERATION.CREATE_TIME.desc(),
                TMinerOperation.class);
    }

    public Map<String, TMinerOperation> mapTMinerOperationByTxHash(List<String> txHash) {
        Map<String, TMinerOperation> map = Maps.newHashMap();
        if (txHash == null) {
            return map;
        }
        for (String hash : txHash) {
            Optional<TMinerOperation> optional = getTMinerOperationByTxHash(hash);
            if (optional.isPresent()) {
                TMinerOperation result = optional.get();
                map.put(hash, result);
            }
        }
        return map;
    }

    /**
     * Check whether the registration information of the current round user exists
     *
     * @param round         round
     * @param address       user address
     * @param operationType operation type
     * @return exits or not
     */
    public boolean applyExist(long round, String address, int operationType) {
        if (operationType == MinerOperationType.SignUp.getCode()) {
            round++;
        }
        Condition condition = Tables.T_MINER_OPERATION
                .ROUND.eq(round)
                .and(Tables.T_MINER_OPERATION.OPERATOR.eq(address))
                .and(Tables.T_MINER_OPERATION.OPERATION_TYPE.eq((byte) operationType));

        List<TMinerOperation> operationList = dSLContext.select()
                .from(Tables.T_MINER_OPERATION)
                .where(condition)
                .fetchInto(TMinerOperation.class);

        if (operationList.isEmpty()) {
            return false;
        }

        for (TMinerOperation operation : operationList) {
            if ((int) operation.getTxStatus() == TxStatus.Pending.ordinal() || (int) operation.getTxStatus() == TxStatus.Success.ordinal()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Check whether the registration information of the current round user exists
     *
     * @param round         round
     * @param proposalId    proposal id
     * @param address       user address
     * @param operationType operation type
     * @return exist or not
     */
    public boolean applyExist(long round, long proposalId, String address, int operationType) {
        if (operationType == MinerOperationType.SignUp.getCode()) {
            round++;
        }
        Condition condition = Tables.T_MINER_OPERATION
                .ROUND.eq(round)
                .and(Tables.T_MINER_OPERATION.OPERATOR.eq(address))
                .and(Tables.T_MINER_OPERATION.OPERATION_TYPE.eq((byte) operationType));

        List<TMinerOperation> list = dSLContext.select()
                .from(Tables.T_MINER_OPERATION)
                .where(condition)
                .fetchInto(TMinerOperation.class);

        if (list.isEmpty()) {
            return false;
        }
        for (TMinerOperation operation : list) {
            Long proposalIdInDb = JSON.parseObject(operation.getAdditionalInfo()).getLong(OperationAdditionalKey.PROPOSAL_ID);
            if (proposalIdInDb != null && proposalId == proposalIdInDb) {
                if ((int) operation.getTxStatus() == TxStatus.Pending.ordinal() || (int) operation.getTxStatus() == TxStatus.Success.ordinal()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<TMinerOperation> getTMinerOperationByTxHash(String txHash) {
        Condition condition = Tables.T_MINER_OPERATION.TX_HASH.eq(txHash);
        TMinerOperation result = dSLContext.select()
                .from(Tables.T_MINER_OPERATION)
                .where(condition)
                .fetchOneInto(TMinerOperation.class);

        return Optional.ofNullable(result);
    }

    @Override
    public void modifyTxHash(long id, String txHash) {
        dSLContext.update(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.TX_HASH, txHash)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, TimeUtil.currentSeconds())
                .where(Tables.T_MINER_OPERATION.ID.eq(id))
                .execute();
    }

    @Override
    public long insert(Map<String, Object> args) {
        long round = (long) args.get("round");
        int operationType = (int) args.get("operationType");
        //  if action type is sign up, round +1
        if (operationType == MinerOperationType.SignUp.getCode()) {
            round++;
        }
        Map<String, Object> additionalInfoMap = (Map<String, Object>) args.get("additionalInfoMap");
        String operator = (String) args.get("operator");

        long id = globalIdService.nextId();
        long now = TimeUtil.currentSeconds();
        JSONObject additionalInfoJson = new JSONObject();
        additionalInfoMap.forEach((k, v) -> additionalInfoJson.put(k, v));

        dSLContext.insertInto(Tables.T_MINER_OPERATION)
                .set(Tables.T_MINER_OPERATION.ID, id)
                .set(Tables.T_MINER_OPERATION.OPERATION_TYPE, (byte) operationType)
                .set(Tables.T_MINER_OPERATION.ROUND, round)
                .set(Tables.T_MINER_OPERATION.ADDITIONAL_INFO, additionalInfoJson.toJSONString())
                .set(Tables.T_MINER_OPERATION.OPERATOR, operator)
                .set(Tables.T_MINER_OPERATION.CREATE_TIME, now)
                .set(Tables.T_MINER_OPERATION.UPDATE_TIME, now)
                .execute();

        return id;
    }

    @Override
    public void drop(long id) {
        dSLContext.delete(Tables.T_MINER_OPERATION)
                .where(Tables.T_MINER_OPERATION.ID.eq(id))
                .execute();
    }

    public List<TMinerOperation> listAssetProposal(int operationType) {
        Condition condition = Tables.T_MINER_OPERATION
                .OPERATION_TYPE.eq((byte) operationType);
        List<TMinerOperation> list = dSLContext.select()
                .from(Tables.T_MINER_OPERATION)
                .where(condition)
                .fetchInto(TMinerOperation.class);

        return list;
    }

    public List<String> listUnknownTransactions() {
        Condition condition = Tables.T_MINER_OPERATION.TX_STATUS.eq((byte) TxStatus.Pending.ordinal());
        List<String> list = dSLContext.select(Tables.T_MINER_OPERATION.TX_HASH)
                .from(Tables.T_MINER_OPERATION)
                .where(condition)
                .fetchInto(String.class);
        return list;
    }

    public void updateTxStatusByTxHashList(List<String> txHashList) {
        if (txHashList != null) {
            txHashList.forEach(v -> dSLContext.update(Tables.T_MINER_OPERATION)
                    .set(Tables.T_MINER_OPERATION.TX_STATUS, (byte) TxStatus.ContractRevert.ordinal())
                    .set(Tables.T_MINER_OPERATION.UPDATE_TIME, TimeUtil.currentSeconds())
                    .where(Tables.T_MINER_OPERATION.TX_HASH.eq(v))
                    .execute());
        }
    }
}
