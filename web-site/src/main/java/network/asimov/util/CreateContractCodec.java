package network.asimov.util;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.web3j.utils.Numeric;

public class CreateContractCodec {

    /**
     * Input data of create contract
     *
     * @param templateType Template Category
     * @param templateHash Template Hash
     * @param ctorArgs     Constructor Arguments
     * @return
     */
    public static String encode(int templateType, String templateHash, String ctorArgs) {
        StringBuilder sb = new StringBuilder();

        // Template Category: 2 Bytes
        sb.append(BaseCodec.encodeShort(templateType));

        // Template Hash
        sb.append(BaseCodec.encodeInt(templateHash.length() / 2));
        sb.append(templateHash);

        // Constructor Arguments: Remove the first 8 characters (Remove the first 8 characters)
        sb.append(Numeric.cleanHexPrefix(ctorArgs).substring(8));

        return sb.toString();
    }

    public static Triple<Integer, String, String> decode(String inputData) {
        int idx = 0;
        Integer templateType = Numeric.toBigInt(inputData.substring(idx, idx + 4)).intValue();
        idx += 4;
        Integer hashLen = Numeric.toBigInt(inputData.substring(idx, idx + 8)).intValue();
        hashLen *= 2;
        idx += 8;
        String templateHash = inputData.substring(idx, idx + hashLen);
        idx += hashLen;
        String ctorArgs = inputData.substring(idx);

        return ImmutableTriple.of(templateType, templateHash, ctorArgs);
    }

}
