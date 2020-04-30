package network.asimov.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sunmengyuan
 * @date 2019-11-19
 */
public class AddressUtil {
    private static Pattern addressPattern = Pattern.compile("^0x6[3|6][a-fA-F0-9]{40}$");

    public static boolean validateAddress(String address) {
        if (address == null) {
            return false;
        }
        Matcher matcher = addressPattern.matcher(address);
        return matcher.matches();
    }
}
