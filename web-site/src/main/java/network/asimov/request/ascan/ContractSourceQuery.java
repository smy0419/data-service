package network.asimov.request.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhangjing
 * @date 2019-11-06
 */
@Data
public class ContractSourceQuery {
    @NotNull(message = "category not null")
    @ApiModelProperty(value = "Template Category", example = "1", required = true)
    private Integer category;

    @NotBlank(message = "name not blank")
    @ApiModelProperty(value = "Template Name", example = "template name", required = true)
    private String name;
}
