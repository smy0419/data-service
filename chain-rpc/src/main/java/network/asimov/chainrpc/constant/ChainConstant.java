package network.asimov.chainrpc.constant;

/**
 * @author zhangjing
 * @date 2019-09-23
 */
public class ChainConstant {
    /**
     * The address of genesis organization contract.
     */
    public static final String GENESIS_ORGANIZATION_ADDRESS = "0x630000000000000000000000000000000000000064";

    /**
     * The binary string of ASIM.
     */
    public static final String ASSET_ASIM = "000000000000000000000000";

    /**
     * The address of validator committee contract.
     */
    public static final String VALIDATOR_COMMITTEE_ADDRESS = "0x63000000000000000000000000000000000000006b";

    /**
     * The address of consensus management contract.
     */
    public static final String CONSENSUS_MANAGEMENT_ADDRESS = "0x63000000000000000000000000000000000000006a";

    /**
     * The name of chain rpc interface.
     * Here is callReadOnlyFunction.
     */
    public static final String RPC_CALL_READ_ONLY_FUNCTION = "callReadOnlyFunction";

    /**
     * The address's bit size of chain.
     */
    public static final int ADDRESS_BIT_SIZE = 168;
}
