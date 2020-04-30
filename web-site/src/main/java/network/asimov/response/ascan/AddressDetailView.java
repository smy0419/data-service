package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import network.asimov.response.common.AssetView;

/**
 * @author zhangjing
 * @date 2019-11-04
 */
@Builder
@Data
public class AddressDetailView {
    @ApiModelProperty(value = "Address")
    private String address;

    @ApiModelProperty(value = "Transaction Count")
    @JsonProperty(value = "tx_count")
    private long txCount;

    @ApiModelProperty(value = "Balance of Address")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AssetView asim;
}
