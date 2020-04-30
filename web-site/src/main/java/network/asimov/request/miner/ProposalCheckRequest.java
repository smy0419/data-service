package network.asimov.request.miner;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2019-11-01
 */
@Data
public class ProposalCheckRequest {
    @NotBlank(message = "asset not blank")
    @ApiModelProperty(value = "Asset", example = "0x000000000000000000000000", required = true)
    @JsonProperty("asset")
    private String asset;
}
