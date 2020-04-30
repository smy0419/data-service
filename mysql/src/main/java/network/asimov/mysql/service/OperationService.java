package network.asimov.mysql.service;

import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-10-16
 */
public interface OperationService {
    /**
     * set tx_hash by id
     *
     * @param id     record id in database
     * @param txHash new tx_hash
     */
    void modifyTxHash(long id, String txHash);

    /**
     * delete record by id
     *
     * @param id record id
     */
    void drop(long id);

    /**
     * save record
     *
     * @param args record arguments map
     * @return
     */
    long insert(Map<String, Object> args);
}
