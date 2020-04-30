package network.asimov.behavior.check;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangjing
 * @date 2019-10-17
 */
public class CheckArgsUtil {
    /**
     * @param address address
     * @return
     */
    public static Map<String, Object> generate(String address) {
        Map<String, Object> args = new HashMap<>(1);
        args.put("address", address);
        return args;
    }

    public static Map<String, Object> generateKey(String key) {
        Map<String, Object> args = new HashMap<>(1);
        args.put("key", key);
        return args;
    }

    /**
     * @param address    address
     * @param proposalId proposal ID
     * @return
     */
    public static Map<String, Object> generate(String address, long proposalId) {
        Map<String, Object> args = new HashMap<>(2);
        args.put("address", address);
        args.put("proposalId", proposalId);
        return args;
    }

    /**
     * @param address   current user address
     * @param candidate candidate address
     * @return
     */
    public static Map<String, Object> generate(String address, String candidate) {
        Map<String, Object> args = new HashMap<>(3);
        args.put("address", address);
        args.put("candidate", candidate);
        return args;
    }


    public static Map<String, Object> generateProposal(String address, Integer proposalType, Long amount, String targetAddress) {
        Map<String, Object> args = new HashMap<>(3);
        args.put("address", address);
        args.put("proposalType", proposalType);
        args.put("amount", amount);
        args.put("targetAddress", targetAddress);
        return args;
    }

    public static Map<String, Object> generateProposal(String address, String asset) {
        Map<String, Object> args = new HashMap<>(2);
        args.put("address", address);
        args.put("asset", asset);
        return args;
    }

    public static Map<String, Object> generateOrgName(String name) {
        Map<String, Object> args = new HashMap<>(2);
        args.put("orgName", name);
        return args;
    }

    public static Map<String, Object> generateContract(String address, String contractAddress) {
        Map<String, Object> args = new HashMap<>(2);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        return args;
    }

    public static Map<String, Object> generateOrg(String address, String contractAddress, String newOrgName, String modifyTarget) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        args.put("newOrgName", newOrgName);
        args.put("modifyTarget", modifyTarget);
        return args;
    }

    public static Map<String, Object> generateAsset(String address, String contractAddress, String assetName) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        args.put("assetName", assetName);
        return args;
    }

    public static Map<String, Object> generateExpense(String address, String contractAddress, String asset, Integer assetType, Long amount) {
        Map<String, Object> args = new HashMap<>(8);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        args.put("asset", asset);
        args.put("assetType", assetType);
        args.put("amount", amount);
        return args;
    }

    public static Map<String, Object> generateMember(String address, String contractAddress, String targetPerson, Integer changeType) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        args.put("targetAddress", targetPerson);
        args.put("changeType", changeType);
        return args;
    }

    public static Map<String, Object> generateConfirmMember(String address, String contractAddress, Integer changeType) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        args.put("changeType", changeType);
        return args;
    }

    public static Map<String, Object> generateVote(String address, String contractAddress, Long proposalId) {
        Map<String, Object> args = new HashMap<>(4);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        args.put("proposalId", proposalId);
        return args;
    }

    public static Map<String, Object> generateMintAsset(String address, String contractAddress, String asset, Integer assetType, Long amountOrVoucherId) {
        Map<String, Object> args = new HashMap<>(8);
        args.put("address", address);
        args.put("contractAddress", contractAddress);
        args.put("asset", asset);
        args.put("assetType", assetType);
        args.put("amountOrVoucherId", amountOrVoucherId);
        return args;
    }

}
