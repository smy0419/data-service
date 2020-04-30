package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import network.asimov.mongodb.entity.common.AssetSummary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-11-04
 */
@Document(collection = "address_transaction")
public class AddressTransaction extends TransactionList {
}
