package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhangjing
 * @date 2019-09-21
 */
@Data
public class AddressPageQuery {
    @Valid
    @ApiModelProperty(required = true)
    @NotNull(message = "page required")
    private PageQuery page;

    @NotBlank(message = "address not blank")
    @ApiModelProperty(value = "Address", example = "0x66e3054b411051da5492aec7a823b00cb3add772d7", required = true)
    private String address;
}
