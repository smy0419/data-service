package network.asimov.response.ascan;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-11-02
 */
@Builder
@Data
public class TransactionCountView {
    @ApiModelProperty(value = "Transaction Count")
    @JsonProperty(value = "tx_count")
    private long txCount;
}
