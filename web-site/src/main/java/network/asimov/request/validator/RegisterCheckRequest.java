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
public class RegisterCheckRequest {
    @Size(min = 1, max = 20, message = "Names range in length from 1 to 20 characters")
    @NotBlank(message = "register name not blank")
    @ApiModelProperty(value = "Register Name", example = "Rocket", required = true)
    @JsonProperty("register_name")
    private String registerName;
}
