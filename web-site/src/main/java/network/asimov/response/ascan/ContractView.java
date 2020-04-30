package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-11-04
 */
@Builder
@Data
public class ContractView {
    @ApiModelProperty(value = "Contract Address")
    private String address;

    @ApiModelProperty(value = "Cumber of Transactions Involving Contracts")
    @JsonProperty(value = "tx_count")
    private long txCount;

    @ApiModelProperty(value = "Time of Contract Creation")
    private long time;

    @ApiModelProperty(value = "Contract Creator")
    private String creator;
}
