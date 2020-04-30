package network.asimov.mysql.constant;

import lombok.AllArgsConstructor;

/**
 * @author sunmengyuan
 * @date 2020-01-15
 */
@AllArgsConstructor
public class DaoAsset {

    public enum Status {
        /**
         * Init: Initialize
         * Success: Issue Success
         * Failed: Issue Failed
         */
        Init, Success, Failed
    }

    public enum Type {
        /**
         * Divisible: Divisible Asset
         * Indivisible: Indivisible Asset
         */
        Divisible, Indivisible
    }

}
