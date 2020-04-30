package network.asimov.response.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-11-04
 */
@Builder
@Data
public class AddressBalanceView {
    @ApiModelProperty(value = "Balance of Assets")
    private List<AssetView> balances;
}
