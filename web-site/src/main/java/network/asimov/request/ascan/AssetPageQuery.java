package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-11-07
 */
@Data
public class AssetPageQuery {
    @Valid
    @ApiModelProperty(required = true)
    @NotNull(message = "page required")
    private PageQuery page;

    @NotBlank(message = "asset not blank")
    @ApiModelProperty(value = "Asset", example = "000000000000000000000000", required = true)
    private String asset;
}
