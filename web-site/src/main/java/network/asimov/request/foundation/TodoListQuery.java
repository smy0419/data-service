package network.asimov.request.foundation;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-10-25
 */
@Data
public class TodoListQuery {
    @Valid
    @ApiModelProperty(required = true)
    @NotNull(message = "page required")
    private PageQuery page;

    @ApiModelProperty(value = "Proposal Type: 0-elect 1-impeach 2-invest", example = "1")
    @JsonProperty("proposal_type")
    private Integer proposalType;
}
