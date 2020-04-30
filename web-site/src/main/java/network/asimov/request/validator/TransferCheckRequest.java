package network.asimov.request.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import network.asimov.validator.annotation.AddressValidate;

/**
 * @author sunmengyuan
 * @date 2019-11-21
 */
@Data
public class TransferCheckRequest {
    @AddressValidate
    @ApiModelProperty(value = "Target Address", example = "0x66e3054b411051da5492aec7a823b00cb3add772d7", required = true)
    @JsonProperty("target_address")
    private String targetAddress;
}
