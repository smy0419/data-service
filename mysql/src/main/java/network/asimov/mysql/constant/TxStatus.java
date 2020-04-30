package network.asimov.mysql.constant;

/**
 * @author zhangjing
 * @date 2019-09-27
 */
public enum TxStatus {
    /**
     * Pending: Unconfirmed
     * Success: Transaction confirmed, Contract execution success
     * ContractRevert: Transaction confirmed, Contract execution failed
     * NoChain: Local Action
     */
    Pending, Success, ContractRevert, NoChain
}
