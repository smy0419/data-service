package network.asimov.behavior.sendtx;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-10-17
 */
public class SendTxArgsUtil {

    public static Map<String, Object> generate(int operationType, Map<String, Object> additionalInfo, String operator) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("operationType", operationType);
        args.put("additionalInfoMap", additionalInfo);
        args.put("operator", operator);

        return args;
    }

    public static Map<String, Object> generate(long round, int operationType, Map<String, Object> additionalInfo, String operator) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("round", round);
        args.put("operationType", operationType);
        args.put("additionalInfoMap", additionalInfo);
        args.put("operator", operator);

        return args;
    }

    public static Map<String, Object> generate(int operationType, Map<String, Object> additionalInfo, String operator, String contractAddress) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("operationType", operationType);
        args.put("additionalInfoMap", additionalInfo);
        args.put("operator", operator);
        args.put("contractAddress", contractAddress);

        return args;
    }
}
