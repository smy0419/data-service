package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2020-01-10
 */
@Data
@Builder
public class CreateOrgParamView {
    @JsonProperty("generated_data")
    @ApiModelProperty(value = "Data")
    private String generatedData;

    @JsonProperty("vote_template_name")
    @ApiModelProperty(value = "Vote Template Name")
    private String voteTemplateName;
}
