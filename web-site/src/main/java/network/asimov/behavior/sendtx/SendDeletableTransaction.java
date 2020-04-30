package network.asimov.behavior.sendtx;

import lombok.Builder;
import network.asimov.chainrpc.service.TransactionService;
import network.asimov.mysql.service.OperationService;
import network.asimov.response.ResultView;
import network.asimov.response.common.TxView;

import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-10-16
 */

@Builder
public class SendDeletableTransaction implements SendTransactionBehavior {
    private TransactionService transactionService;
    private OperationService operationService;

    @Override
    public ResultView<TxView> perform(String callData, Map<String, Object> args) {
        // Insert operation record
        long id = operationService.insert(args);

        // Send the transaction up the chain
        try {
            String txHash = transactionService.sendRawTransaction(callData);
            // Up the chain successful，fill in the transaction Hash
            operationService.modifyTxHash(id, txHash);
            return ResultView.ok(TxView.builder().txHash(txHash).build());
        } catch (Exception e) {
            // Up the chain failed，delete operation record
            operationService.drop(id);
            throw e;
        }
    }

}
