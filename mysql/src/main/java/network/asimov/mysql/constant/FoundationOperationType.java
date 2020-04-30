package network.asimov.mysql.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sunmengyuan
 * @date 2019-10-14
 */
@AllArgsConstructor
public enum FoundationOperationType {
    Proposal(1),
    Vote(2),
    Donate(3);

    @Getter
    private int code;
}
