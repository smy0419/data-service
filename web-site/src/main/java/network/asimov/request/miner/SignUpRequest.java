package network.asimov.request.miner;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author zhangjing
 * @date 2019-09-29
 */
@Data
public class SignUpRequest {
    @NotBlank(message = "declaration not blank")
    @Size(max = 100, message = "declaration max size 100")
    @ApiModelProperty(value = "Sign Up Declaration", example = "I wanna be...", required = true)
    private String declaration;

    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;
}
