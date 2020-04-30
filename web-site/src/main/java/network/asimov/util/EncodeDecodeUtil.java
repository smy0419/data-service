package network.asimov.util;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint32;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author sunmengyuan
 * @date 2020-01-03
 */
public class EncodeDecodeUtil {

    public static String encodeConstructor(String name, String vote) {
        Function function = new Function(
                "constructor",
                Arrays.asList(new Utf8String(name),
                        DynamicArray.empty(Address.TYPE_NAME),
                        new Utf8String(vote)),
                Collections.emptyList()
        );
        return FunctionEncoder.encode(function);
    }


    public static String encodeCreateAsset(String assetName, String assetSymbol, String assetDesc,
                                           long assetType, long assetIndex, BigInteger total) {
        Function function = new Function(
                "createAsset",
                Arrays.asList(
                        new Utf8String(assetName),
                        new Utf8String(assetSymbol),
                        new Utf8String(assetDesc),
                        new Uint32(assetType),
                        new Uint32(assetIndex),
                        new Uint(total)),
                Collections.emptyList()
        );
        return FunctionEncoder.encode(function);
    }

}
