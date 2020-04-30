package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-11-06
 */
@Builder
@Data
public class ContractSourceView {
    @ApiModelProperty(value = "Template Category")
    private int category;

    @ApiModelProperty(value = "Template Name")
    @JsonProperty(value = "template_name")
    private String templateName;

    @ApiModelProperty(value = "Byte Code")
    @JsonProperty(value = "byte_code")
    private String byteCode;

    @ApiModelProperty(value = "ABI")
    private String abi;

    @ApiModelProperty(value = "Source Code")
    private String source;
}
