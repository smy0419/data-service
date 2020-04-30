package network.asimov.request.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Data
public class ManageCheckRequest {
    @Size.List(@Size(min = 1, max = 20, message = "list size must between 1 and 20"))
    @ApiModelProperty(value = "Validator Address List", example = "[\"0x123\",\"0x456\"]", required = true)
    @JsonProperty("addresses")
    private List<String> validatorList;
}
