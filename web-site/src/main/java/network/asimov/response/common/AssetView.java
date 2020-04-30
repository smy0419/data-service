package network.asimov.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjing
 * @date 2019-09-20
 */
@Data
@Builder
public class AssetView {
    @ApiModelProperty(value = "Asset ID")
    private String asset;

    @ApiModelProperty(value = "Amount")
    private String value;

    @ApiModelProperty(value = "Asset Name")
    @Builder.Default
    private String name = StringUtils.EMPTY;

    @ApiModelProperty(value = "Asset Symbol")
    @Builder.Default
    private String symbol = StringUtils.EMPTY;

    @ApiModelProperty(value = "Asset Description")
    @Builder.Default
    private String description = StringUtils.EMPTY;

    @ApiModelProperty(value = "Asset Logo")
    @Builder.Default
    private String logo = StringUtils.EMPTY;

    @ApiModelProperty(value = "Indivisible List")
    @JsonProperty(value = "indivisible_list")
    @Builder.Default
    private List<Indivisible> indivisibleList = new ArrayList<>();

    @Data
    @Builder
    public static class Indivisible {
        @ApiModelProperty(value = "Asset Number")
        private String number;

        @ApiModelProperty(value = "Asset Description")
        @Builder.Default
        private String description = StringUtils.EMPTY;
    }
}
