package network.asimov.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import network.asimov.error.ErrorCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhangjing
 * @date 2019-03-09 18:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultView<T> {
    @ApiModelProperty(value = "Return Code: 0-normal，!0-abnormal")
    private int code;

    @ApiModelProperty(value = "Return Message")
    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResultView<T> ok() {
        return new ResultView<>(0, StringUtils.EMPTY, null);
    }

    public static <T> ResultView<T> ok(T data) {
        return new ResultView<>(0, StringUtils.EMPTY, data);
    }

    public static <T> ResultView<T> error(ErrorCode httpResponseCode) {
        return new ResultView<>(httpResponseCode.getCode(), httpResponseCode.getMsg(), null);
    }

    public static <T> ResultView<T> error(int code, String msg) {
        return new ResultView<>(code, msg, null);
    }

//    public static <T> ResultView<T> judgeAction(boolean success) {
//        if (success) {
//            return ResultView.ok();
//        } else {
//            return ResultView.error(ErrorCode.ACTION_FAILED);
//        }
//    }

    public boolean success() {
        return code == 0;
    }

//    public static <T> ResultView<T> error(int code, String msg) {
//        return new ResultView<>(code, msg, null);
//    }
}

