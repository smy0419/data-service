package network.asimov.mongodb.entity.ascan;

import lombok.*;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Asset Issuance Record
 *
 * @author zhangjing
 * @date 2019-09-20
 */

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "asset_issue")
public class AssetIssue extends BaseEntity {
    /**
     * Asset String
     */
    private String asset;

    /**
     * Issue Type: Create or Mint
     */
    @Field(value = "issue_type")
    private Integer issueType;

    /**
     * Issue Number
     */
    private Long value;

    @AllArgsConstructor
    public enum IssueType {
        /**
         * Create Asset
         */
        Create(1),

        /**
         * Mint Asset
         */
        Mint(2);

        @Getter
        private int code;
    }
}


