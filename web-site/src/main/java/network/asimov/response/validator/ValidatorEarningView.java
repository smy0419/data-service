package network.asimov.response.validator;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-11-18
 */
@Data
@Builder
public class ValidatorEarningView {
    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Earning List")
    private List<EarningAssetView> earning;
}
