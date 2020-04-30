package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.request.PageQuery;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-12-11
 */
@Data
public class MessagePageQuery {
    @ApiModelProperty(value = "Contract Address", required = true)
    @NotEmpty(message = "contract_address not blank")
    @JsonProperty("contract_address")
    private String contractAddress;

    @Valid
    @ApiModelProperty(required = true)
    @NotNull(message = "page required")
    private PageQuery page;
}
