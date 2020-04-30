package network.asimov.response.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class TotalEarningView {
    @ApiModelProperty(value = "Total Earning")
    @JsonProperty(value = "total_earning")
    public List<EarningAssetView> totalEarning;
}
