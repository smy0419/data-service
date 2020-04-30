package network.asimov.request.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Data
public class RegisterRequest {
    @NotBlank(message = "call_data not blank")
    @ApiModelProperty(value = "Transaction Data", example = "call data", required = true)
    @JsonProperty("call_data")
    private String callData;

    @NotBlank(message = "register name not blank")
    @Size(max = 20, message = "register name max size 20")
    @ApiModelProperty(value = "Register Name", example = "Rocket", required = true)
    @JsonProperty("register_name")
    private String registerName;
}
