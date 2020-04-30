package network.asimov.mysql.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author sunmengyuan
 * @date 2020-01-04
 */
@Data
@Builder
public class Account {
    private Long id;

    private String address;

    private String avatar;

    private String name;

    private Long createTime;

    private Long modifyTime;
}
