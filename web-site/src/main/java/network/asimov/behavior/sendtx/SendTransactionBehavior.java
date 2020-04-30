package network.asimov.behavior.sendtx;

import network.asimov.response.ResultView;
import network.asimov.response.common.TxView;

import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-10-16
 */
public interface SendTransactionBehavior {
    /**
     * Send transaction behavior
     *
     * @param args arguments
     * @return Transaction Hash
     */
    public ResultView<TxView> perform(String callData, Map<String, Object> args);
}
