package network.asimov.util;

import org.apache.commons.lang3.StringUtils;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * 基础类型的编解码
 *
 * @author The flow developers
 */
public class BaseCodec {

    public static String encodeString(String content) {
        try {
            byte[] payload = content.getBytes("utf-8");
            return Numeric.toHexStringNoPrefix(payload);
        } catch (Exception e) {
            return null;
        }
    }

    public static String encodeInt(int value) {
        byte[] payload = new byte[4];
        for (int i = 0; i < 4; i++) {
            payload[i] = (byte) (value >> 8 * (3 - i));
        }
        return Numeric.toHexStringNoPrefix(payload);
    }

    public static String encodeShort(int value) {
        byte[] payload = new byte[2];
        for (int i = 0; i < 2; i++) {
            payload[i] = (byte) (value >> 8 * (1 - i));
        }
        return Numeric.toHexStringNoPrefix(payload);
    }

    public static BigInteger decodeUint(String data) {
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        List<TypeReference<?>> logList = Arrays.asList(
                new TypeReference<Uint>() {
                }
        );
        List<Type> results = FunctionReturnDecoder.decode(data, Utils.convert(logList));
        BigInteger value = (BigInteger) results.get(0).getValue();
        return value;
    }

}

