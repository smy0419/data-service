package network.asimov.response.validator;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2019-11-27
 */
@Data
@Builder
public class BtcMinerView {
    @ApiModelProperty(value = "BTC Miner Address")
    private String address;

    @ApiModelProperty(value = "Mining Pool Domain")
    private String domain;

}
