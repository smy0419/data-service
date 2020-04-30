package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangjing
 * @date 2019-11-04
 */
@Data
public class AddressQuery {
    @NotBlank(message = "address not blank")
    @ApiModelProperty(value = "Address", example = "0x......", required = true)
    private String address;
}
