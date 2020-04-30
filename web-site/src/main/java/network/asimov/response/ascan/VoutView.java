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
public class VoutView {
    // TODO NEED DELETE
//    @ApiModelProperty(value = "out的金额")
//    private String value;

    @ApiModelProperty(value = "Out Sequence")
    private long n;

    @ApiModelProperty(value = "Script Public Key")
    @JsonProperty(value = "script_pub_key")
    private ScriptPubKeyView scriptPubKey;

//    @ApiModelProperty(value = "额外数据")
//    private String data;

    // TODO NEED DELETE
//    @ApiModelProperty(value = "币种")
//    private String asset;

    @ApiModelProperty(value = "UTXO Asset")
    private AssetView asset;

    @Builder
    @Data
    public static class ScriptPubKeyView {
        @ApiModelProperty(value = "ASM Format of Script Public Key")
        private String asm;

//        @ApiModelProperty(value = "HEX Format of Script Public Key")
//        private String hex;

        @ApiModelProperty(value = "Required Signature")
        @JsonProperty(value = "req_sigs")
        private String reqSigs;

        @ApiModelProperty(value = "Signature Type")
        private String type;

        @ApiModelProperty(value = "Signature Addresses")
        private List<String> addresses;
    }
}