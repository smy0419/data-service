package network.asimov.config;

import network.asimov.behavior.sendtx.SendDeletableTransaction;
import network.asimov.behavior.sendtx.SendTransactionBehavior;
import network.asimov.chainrpc.service.TransactionService;
import network.asimov.mysql.service.OperationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zhangjing
 * @date 2019-10-16
 */
@Configuration
public class BehaviorConfig {
    @Resource(name = "chainRpcTxService")
    private TransactionService transactionService;

    @Resource(name = "minerOperationService")
    private OperationService minerOperationService;

    @Resource(name = "foundationOperationService")
    private OperationService foundationOperationService;

    @Resource(name = "daoOperationService")
    private OperationService daoOperationService;

    @Bean("minerSendDeletableTransaction")
    public SendTransactionBehavior minerSendDeletableTransaction() {
        return SendDeletableTransaction.builder()
                .transactionService(transactionService)
                .operationService(minerOperationService).build();
    }

    @Bean("foundationSendDeletableTransaction")
    public SendTransactionBehavior foundationSendDeletableTransaction() {
        return SendDeletableTransaction.builder()
                .transactionService(transactionService)
                .operationService(foundationOperationService).build();
    }

    @Bean("daoSendDeletableTransaction")
    public SendTransactionBehavior daoSendDeletableTransaction() {
        return SendDeletableTransaction.builder()
                .transactionService(transactionService)
                .operationService(daoOperationService).build();
    }

}
