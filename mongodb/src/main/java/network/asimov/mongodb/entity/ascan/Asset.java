package network.asimov.mongodb.entity.ascan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Asset Information
 *
 * @author zhangjing
 * @date 2019-09-20
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "asset")
public class Asset extends BaseEntity {
    /**
     * Address Of Asset Issue
     */
    @Field(value = "issue_address")
    private String issueAddress;

    /**
     * Asset String
     */
    private String asset;

    /**
     * Asset name
     */
    private String name;

    /**
     * Asset Symbol
     */
    private String symbol;

    /**
     * Asset Description
     */
    private String description;

    /**
     * Asset Logo
     */
    private String logo;
}
