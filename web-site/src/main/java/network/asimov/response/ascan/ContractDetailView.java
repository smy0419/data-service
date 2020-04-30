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
public class ContractDetailView {
    @ApiModelProperty(value = "Contract Address")
    private String address;

    @ApiModelProperty(value = "Transaction Hash of Contract Creation")
    @JsonProperty(value = "tx_hash")
    private String txHash;

    @ApiModelProperty(value = "Count of Transactions Involving Contract")
    @JsonProperty(value = "tx_count")
    private long txCount;

    @ApiModelProperty(value = "Time of Contract Creation")
    private long time;

    @ApiModelProperty(value = "Contract Creator")
    private String creator;

    @ApiModelProperty(value = "Template Type")
    @JsonProperty(value = "template_type")
    private int templateType;

    @ApiModelProperty(value = "Template Name")
    @JsonProperty(value = "template_name")
    private String templateName;

    @ApiModelProperty(value = "Asimov of Contract")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AssetView asim;
}
