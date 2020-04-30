package network.asimov.config;

import lombok.extern.slf4j.Slf4j;
import network.asimov.error.BusinessException;
import network.asimov.error.ErrorCode;
import network.asimov.response.ResultView;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 统一处理异常类
 *
 * @author zhangjing
 * @date 2019-03-09 23:36
 */

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultView defaultErrorHandler(HttpServletRequest httpServletRequest, Exception e) {

        ResultView resultView;
        if (e instanceof MethodArgumentNotValidException) {
            String message = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors().get(0).getDefaultMessage();
            resultView = ResultView.error(ErrorCode.PARAMETER_INVALID.getCode(),
                    String.format(ErrorCode.PARAMETER_INVALID.getMsg(), message));
        } else if (e instanceof ServletRequestBindingException) {
            log.error("The request url is {}, MISSING_TOKEN", httpServletRequest.getRequestURI());
            resultView = ResultView.error(ErrorCode.MISSING_TOKEN);
        } else if (e instanceof BusinessException) {
            log.error("The request url is {}, BUSINESS_EXCEPTION as follows:", httpServletRequest.getRequestURI(), e);
            resultView = ResultView.error(((BusinessException) e).getCode(), ((BusinessException) e).getMsg());
        } else {
            log.error("The request url is {}, INTERNAL_SERVER_ERROR as follows:", httpServletRequest.getRequestURI(), e);
            resultView = ResultView.error(ErrorCode.UNKNOWN_ERROR);
        }
        return resultView;
    }
}

