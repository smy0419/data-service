package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RawTxView {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "Block Height")
    private Long height;

    @ApiModelProperty(value = "Block Hash")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "block_hash")
    private String blockHash;

    @ApiModelProperty(value = "Transaction Confirmations")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long confirmations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(value = "Gas Limit")
    @JsonProperty(value = "gas_limit")
    private Long gasLimit;

    @ApiModelProperty(value = "Transaction Hash")
    private String hash;

    @ApiModelProperty(value = "Virtual Transaction Hash")
    @JsonProperty(value = "vtx_hash")
    private String vtxHash;

    @ApiModelProperty(value = "Transaction Size")
    private int size;

    @ApiModelProperty(value = "Transaction Time")
    private long time;

    @ApiModelProperty(value = "Transaction Fee")
    private List<AssetView> fee;

    @ApiModelProperty(value = "Vin")
    private List<VinView> vin;

    @ApiModelProperty(value = "vout")
    private List<VoutView> vout;
}
