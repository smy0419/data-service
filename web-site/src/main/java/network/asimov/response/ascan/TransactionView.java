package network.asimov.response.ascan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

import java.util.List;

/**
 * @author zhangjing
 * @date 2019-10-31
 */

@Builder
@Data
public class TransactionView {
    @ApiModelProperty(value = "Transaction Hash")
    private String hash;

    @ApiModelProperty(value = "Transaction Time")
    private long time;

    @ApiModelProperty(value = "Transaction Fee")
    private List<AssetView> fee;
}
