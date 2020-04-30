package network.asimov.mysql.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sunmengyuan
 * @date 2019-09-26
 */
@AllArgsConstructor
public enum MinerOperationType {
    Proposal(1),
    SignUp(2),
    Vote(3);

    @Getter
    private int code;
}

