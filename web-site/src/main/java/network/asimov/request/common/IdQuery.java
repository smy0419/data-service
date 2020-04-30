package network.asimov.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-09-29
 */
@Data
public class IdQuery {
    @NotNull(message = "proposal id required")
    @ApiModelProperty(value = "Proposal ID", example = "1", required = true)
    private Long id;
}
