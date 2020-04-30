package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author sunmengyuan
 * @date 2019-12-10
 */
@Data
public class PrepareCreateOrgRequest {
    @ApiModelProperty(value = "Organization Name", required = true)
    @NotBlank(message = "org_name name not blank")
    @Size(max = 20, message = "org_name name max size 20")
    @JsonProperty("org_name")
    private String orgName;
}
