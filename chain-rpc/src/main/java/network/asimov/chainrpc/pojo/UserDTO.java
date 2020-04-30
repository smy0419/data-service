package network.asimov.chainrpc.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author zhangjing
 * @date 2019-09-26
 */
@Data
@Builder
public class UserDTO {
    private String address;
    private String name;
    private String icon;
}
