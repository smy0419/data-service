package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-31
 */

@Builder
@Data
public class VinView {
    @ApiModelProperty(value = "Coin Base Transaction")
    @JsonProperty(value = "coin_base")
    private String coinBase;

    @ApiModelProperty(value = "The Maximum Value of Uint32")
    private long sequence;

    @ApiModelProperty(value = "Transaction Hash of UTXO")
    @JsonProperty(value = "out_tx_hash")
    private String outTxHash;

    @ApiModelProperty(value = "Out Sequence of UTXO Transaction Output")
    private long vout;

    @ApiModelProperty(value = "Transaction Signature of UTXO Transaction")
    @JsonProperty(value = "script_sig")
    private ScriptSigView scriptSig;

    @ApiModelProperty(value = "Pre-Out Detail Information")
    @JsonProperty(value = "prev_out")
    private PrevOutView prevOutView;

    @Builder
    @Data
    public static class ScriptSigView {
        @ApiModelProperty(value = "ASM Format of Signature")
        private String asm;

        @ApiModelProperty(value = "HEX Format of Signature")
        private String hex;
    }

    @Builder
    @Data
    public static class PrevOutView {
        @ApiModelProperty(value = "UTXO Addresses")
        private List<String> addresses;

        @ApiModelProperty(value = "UTXO Asset")
        private AssetView asset;
        // TODO NEED DELETE
//        @ApiModelProperty(value = "UTXO可用金额")
//        private String value;
//
//        @ApiModelProperty(value = "币种")
//        private String asset;

//        @ApiModelProperty(value = "额外数据")
//        private String data;
    }
}
