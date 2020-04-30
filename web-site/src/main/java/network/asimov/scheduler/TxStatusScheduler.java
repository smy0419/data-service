package network.asimov.scheduler;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import network.asimov.chainrpc.service.TransactionService;
import network.asimov.mysql.service.dorg.DaoOperationService;
import network.asimov.mysql.service.foundation.FoundationOperationService;
import network.asimov.mysql.service.miner.MinerOperationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sunmengyuan
 * @date 2020-03-17
 */
@Component
@Slf4j
public class TxStatusScheduler {
    @Resource(name = "foundationOperationService")
    private FoundationOperationService foundationOperationService;

    @Resource(name = "minerOperationService")
    private MinerOperationService minerOperationService;

    @Resource(name = "daoOperationService")
    private DaoOperationService daoOperationService;

    @Resource(name = "chainRpcTxService")
    private TransactionService transactionService;

    @Scheduled(fixedRate = 300 * 1000)
    public void updateTxStatus() {
        List<String> hashList = Lists.newArrayList();
        List<String> foundation = foundationOperationService.listUnknownTransactions();
        List<String> miner = minerOperationService.listUnknownTransactions();
        List<String> dao = daoOperationService.listUnknownTransactions();

        hashList.addAll(foundation);
        hashList.addAll(miner);
        hashList.addAll(dao);

        List<String> memPoolHashList = transactionService.getMemPoolTransactions(hashList);

        foundationOperationService.updateTxStatusByTxHashList(memPoolHashList);
        minerOperationService.updateTxStatusByTxHashList(memPoolHashList);
        daoOperationService.updateTxStatusByTxHashList(memPoolHashList);
    }
}
