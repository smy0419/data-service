package network.asimov.response.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Data
@Builder
public class ValidatorView {
    @ApiModelProperty(value = "Validator Address")
    private String address;

    @ApiModelProperty(value = "Planned Blocks")
    @JsonProperty(value = "planned_blocks")
    @Builder.Default
    private Long plannedBlocks = -1L;

    @ApiModelProperty(value = "Actual Blocks")
    @JsonProperty(value = "actual_blocks")
    @Builder.Default
    private Long actualBlocks = -1L;

}
