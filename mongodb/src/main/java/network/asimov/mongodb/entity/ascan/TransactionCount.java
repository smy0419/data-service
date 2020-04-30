package network.asimov.mongodb.entity.ascan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author zhangjing
 * @date 2020/4/23
 */

@Data
@Document(collection = "transaction_count")
public class TransactionCount {
    @Id
    private ObjectId id;

    private String key;

    /**
     * Record Time
     */
    private Long time;

    @Field(value = "tx_count")
    private Long txCount;

    private Integer category;

    /**
     * Transaction Hash Which Created Contract
     */
    @Field(value = "tx_hash")
    private String txHash;

    /**
     * Contract Creator Address
     */
    private String creator;

    /**
     * Contract Template Type
     */
    @Field(value = "template_type")
    private Integer templateType;

    /**
     * Contract Template Name
     */
    @Field(value = "template_name")
    private String templateTame;

    @AllArgsConstructor
    public enum TxCountCategory {
        /**
         * Count Address
         */
        Address(1),

        /**
         * Count Transaction
         */
        Contract(2),

        /**
         * Count Asset
         */
        Asset(3);

        @Getter
        private int code;
    }
}
