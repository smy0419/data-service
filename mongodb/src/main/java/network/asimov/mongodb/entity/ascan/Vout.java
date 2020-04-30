package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-30
 */
@Data
public class Vout {
    /**
     * Output Amount
     */
    private Long value;

    /**
     * Output Sequence
     */
    private Long n;

    /**
     * Script Public Key
     */
    @Field(value = "script_pub_key")
    private ScriptPubKey scriptPubKey;

    /**
     * Extra Data
     */
    private String data;

    /**
     * Asset
     */
    private String asset;

    @Data
    public static class ScriptPubKey {
        private String asm;
        @Field(value = "req_sigs")
        private String reqSigs;
        private String type;
        private List<String> addresses;
    }
}