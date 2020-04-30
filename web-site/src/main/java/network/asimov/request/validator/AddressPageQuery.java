package network.asimov.request.validator;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-11-18
 */
@Data
public class AddressPageQuery {
    @Valid
    @ApiModelProperty(required = true)
    @NotNull(message = "page required")
    private PageQuery page;
}