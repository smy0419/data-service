package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-12-17
 */
@Data
public class DaoProposalIdQuery {
    @ApiModelProperty(value = "Proposal ID", example = "1", required = true)
    @NotNull(message = "proposal id required")
    private Long id;

    @ApiModelProperty(value = "Contract Address", example = "0x639e13bfd6ee527ebf78fb2f5d8668e4996f6e895b", required = true)
    @NotNull(message = "contract_address required")
    @JsonProperty("contract_address")
    private String contractAddress;
}
