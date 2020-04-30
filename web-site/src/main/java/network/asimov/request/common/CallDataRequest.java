package network.asimov.request.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author sunmengyuan
 * @date 2019-10-08
 */
@Data
public class CallDataRequest {
    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;
}
