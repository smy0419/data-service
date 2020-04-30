package network.asimov.chainrpc.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-11-07
 */
@Data
@Builder
public class ContractSourceDTO {
    private int category;
    private String templateName;
    private String byteCode;
    private String abi;
    private String source;
}
