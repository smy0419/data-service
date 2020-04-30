package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-30
 */
@Data
public class Vin {
    /**
     * CoinBase Script
     */
    @Field(value = "coin_base")
    private String coinBase;

    /**
     * Uint32 Maximum
     */
    private Long sequence;

    /**
     * Input's Transaction Hash
     */
    @Field(value = "out_tx_hash")
    private String outTxHash;

    /**
     * UTXO's Sequence Of Input's Transaction
     */
    @Field(value = "v_out")
    private Long vOut;

    /**
     * Input's Transaction Signature
     */
    @Field(value = "script_sig")
    private ScriptSig scriptSig;

    /**
     * Input's Detail Information
     */
    @Field(value = "prev_out")
    private PrevOut prevOut;

    @Data
    public static class ScriptSig {
        private String hex;
    }

    @Data
    public static class PrevOut {
        /**
         * UTXO Owner
         */
        private List<String> addresses;

        /**
         * UTXO Amount
         */
        private Long value;

        /**
         * Asset
         */
        private String asset;
    }
}