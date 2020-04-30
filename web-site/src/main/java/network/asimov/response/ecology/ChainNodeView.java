package network.asimov.response.ecology;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-10-24
 */

@Data
@Builder
public class ChainNodeView {
    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "City")
    private String city;

    @ApiModelProperty(value = "Subdivision")
    private String subdivision;

    @ApiModelProperty(value = "Country")
    private String country;

    @ApiModelProperty(value = "Longtitude")
    private String longitude;

    @ApiModelProperty(value = "Latitude")
    private String latitude;
}
