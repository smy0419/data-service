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
public class LocationView {
    @ApiModelProperty(value = "IP")
    private String address;

    @ApiModelProperty(value = "City")
    private String city;

    @ApiModelProperty(value = "Subdivision")
    private String subdivision;

    @ApiModelProperty(value = "Country")
    private String country;

    @ApiModelProperty(value = "Longitude")
    private String longitude;

    @ApiModelProperty(value = "Latitude")
    private String latitude;
}
