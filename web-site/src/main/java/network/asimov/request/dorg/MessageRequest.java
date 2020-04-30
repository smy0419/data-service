package network.asimov.request.dorg;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author sunmengyuan
 * @date 2020-01-17
 */
@Data
public class MessageRequest {
    @NotNull(message = "message id required")
    @ApiModelProperty(value = "Message ID", example = "1", required = true)
    @JsonProperty("message_id")
    private Long messageId;

    @NotNull(message = "message state required")
    @ApiModelProperty(value = "Message Type: 0-unread, 1-read, 2-disagree, 3-agree", example = "1", required = true)
    private Integer state;

}
