package network.asimov.mongodb.entity.validator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import network.asimov.mongodb.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "validator")
public class Validator extends BaseEntity {
    /**
     * Validator Address
     */
    private String address;

    /**
     * Validator IP Address
     */
    private Location location;

    /**
     * Planned Mining Blocks
     */
    @Field(value = "planned_blocks")
    private Long plannedBlocks;

    /**
     * Actually Produced Blocks
     */
    @Field(value = "actual_blocks")
    private Long actualBlocks;

    @Data
    public static class Location {
        private String ip;
        private String city;
        private String subdivision;
        private String country;
        private String longitude;
        private String latitude;
    }

    public enum Role {
        /**
         * No Role
         */
        Nothing,

        /**
         * Validator
         */
        Validator,
    }
}
