package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author zhangjing
 * @date 2019-10-26
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "trading")
public class Trading extends BaseEntity {

    /**
     * Transaction Amount
     */
    private Long value;

    /**
     * Asset String
     */
    private String asset;

}
