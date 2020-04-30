package network.asimov.util;

/**
 * @author zhangjing
 * @date 2019-10-08
 */
public class TimeUtil {
    public static final int SECONDS_OF_DAY = 24 * 60 * 60;

    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
