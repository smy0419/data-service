package network.asimov.response.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

/**
 * @author zhangjing
 * @date 2019-11-08
 */
@Builder
public class UploadView {
    @JsonProperty(value = "resource_url")
    @ApiModelProperty(value = "Resource URL")
    private String resourceUrl;
}
