package network.asimov.behavior.convert;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-28
 */
public interface ConvertBehavior<O, T> {
    T convert(O origin);
    List<T> convert(List<O> origins);
}
