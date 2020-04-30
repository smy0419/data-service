package network.asimov.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author zhangjing
 * @date 2019-09-23
 */
public class AmountUtil {
    private final static BigDecimal UNIT_XING = new BigDecimal(100000000);

    /**
     * Convert ASIM to XING
     *
     * @param asim ASIM string
     * @return XING string
     */
    public static String asimToXing(String asim) {
        return new BigDecimal(asim).multiply(UNIT_XING).setScale(0, RoundingMode.DOWN).toString();
    }

    /**
     * Convert ASIM to Xing
     *
     * @param asim ASIM double
     * @return XING string
     */
    public static String asimToXing(Double asim) {
        return new BigDecimal(asim).multiply(UNIT_XING).setScale(0, RoundingMode.DOWN).toString();
    }
}
