package network.asimov.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-09-23
 */
@Data
@Builder
public class PersonView {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Name")
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "Avatar")
    private String icon;
}
