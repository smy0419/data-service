package network.asimov.constant;

import com.google.common.collect.Maps;
import network.asimov.mongodb.entity.foundation.Proposal;

import java.util.Map;

/**
 * @author sunmengyuan
 * @date 2019-10-15
 */
public class ConditionMap {
    public static Map<Integer, Double> getPassRateMap() {
        Map<Integer, Double> map = Maps.newHashMap();
        map.put(Proposal.Type.Elect.ordinal(), 0.7);
        map.put(Proposal.Type.Impeach.ordinal(), 0.7);
        map.put(Proposal.Type.Expenses.ordinal(), 0.5);
        return map;
    }
}
