package network.asimov.mongodb.entity.validator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.service.BaseService;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author sunmengyuan
 * @date 2019-11-27
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "btc_miner")
public class BtcMiner extends BaseService {
    /**
     * BTC Miner Address
     */
    private String address;
    /**
     * Mining Pool Domain
     */
    private String domain;

}
