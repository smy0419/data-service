package network.asimov.response.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author sunmengyuan
 * @date 2019-11-14
 */
@Data
@Builder
public class EarningView {
    @ApiModelProperty(value = "Total Earning")
    @JsonProperty(value = "total_earning")
    private List<EarningAssetView> totalEarning;

    @ApiModelProperty(value = "24-hours Total Earning")
    @JsonProperty(value = "one_day_total_award")
    private List<EarningAssetView> oneDayEarning;
}
