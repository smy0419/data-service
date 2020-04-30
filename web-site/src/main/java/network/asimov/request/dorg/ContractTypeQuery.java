package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2019-12-10
 */
@Data
public class ContractTypeQuery {
    @ApiModelProperty(value = "Contract Typ: 0-organization, 1-vote", required = true)
    @NotNull(message = "contract_type not null")
    @JsonProperty("contract_type")
    private Integer contractType;
}
