package network.asimov.request.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2020-04-22
 */
@Data
public class AssetNamePageQuery {
    @ApiModelProperty(value = "asset name", example = "asim")
    @JsonProperty("asset_name")
    private String assetName;

    @Valid
    @ApiModelProperty(required = true)
    @NotNull(message = "page required")
    private PageQuery page;
}
