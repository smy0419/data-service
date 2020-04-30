package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sunmengyuan
 * @date 2020-02-14
 */
@Data
@Builder
public class IssueAssetView {
    @ApiModelProperty(value = "Asset")
    private String asset;

    @ApiModelProperty(value = "Asset Type: 0-indivisible, 1-divisible")
    @JsonProperty("asset_type")
    private Integer assetType;

    @ApiModelProperty(value = "Asset Index")
    @JsonProperty("asset_index")
    private Integer assetIndex;

    @ApiModelProperty(value = "Asset Name")
    @Builder.Default
    private String name = StringUtils.EMPTY;

    @ApiModelProperty(value = "Asset Symbol")
    @Builder.Default
    private String symbol = StringUtils.EMPTY;
}
