package network.asimov.error;

import lombok.Builder;
import lombok.Getter;

/**
 * @author zhangjing
 * @date 2019-09-20
 */

public class BusinessException extends RuntimeException {
    @Getter
    private int code;
    @Getter
    private String msg;

    @Builder
    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }
}
