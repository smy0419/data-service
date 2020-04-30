package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2019-11-06
 */
@Data
public class AssetQuery {
    @NotBlank(message = "asset not blank")
    @ApiModelProperty(value = "Asset", example = "000000000000000000000000", required = true)
    private String asset;
}
