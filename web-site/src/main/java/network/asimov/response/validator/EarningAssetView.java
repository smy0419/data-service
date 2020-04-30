package network.asimov.response.validator;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-11-15
 */
@Data
@Builder
public class EarningAssetView {
    @ApiModelProperty(value = "Earning Asset")
    private String asset;

    @ApiModelProperty(value = "Earning Value")
    private String value;

    @ApiModelProperty(value = "Asset Logo")
    private String logo;

    @ApiModelProperty(value = "Asset Name")
    private String name;

    @ApiModelProperty(value = "Asset Symbol")
    private String symbol;
}
