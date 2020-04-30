package network.asimov.behavior.check;

import network.asimov.response.ResultView;

import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-10-16
 */
public interface CheckBehavior {
    /**
     * check behavior
     *
     * @param args arguments
     * @return result
     */
    ResultView check(Map<String, Object> args);
}
