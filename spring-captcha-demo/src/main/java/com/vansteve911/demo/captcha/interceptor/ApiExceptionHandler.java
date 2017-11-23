package com.vansteve911.demo.captcha.interceptor;

import com.vansteve911.demo.captcha.dto.ApiResult;
import com.vansteve911.demo.captcha.exception.BusinessException;
import com.vansteve911.spring.captcha.common.CaptchaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.vansteve911.demo.captcha.exception.ErrorCode.*;

/**
 * Created by vansteve911 on 2018/3/30.
 */
@ControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private List<Handler> handlerChain = Arrays.asList(
            new Handler<>((r, e) -> new ApiResult(FAILED.getCode(), e.getMessage()), CaptchaException.class),
            new Handler<>((r, e) -> new ApiResult(e.getCode(), e.getMsg()), BusinessException.class),
            new Handler<>((r, e) -> new ApiResult(METHOD_NOT_ALLOWED.getCode(), e.getMessage()), HttpRequestMethodNotSupportedException.class),
            new Handler<>((r, e) -> new ApiResult(ILLEGAL_PARAM.getCode(), e.getMessage()), MissingServletRequestParameterException.class));

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    ApiResult handleException(HttpServletRequest request, Exception ex) {
        return handlerChain.stream().map(h -> h.handle(request, ex))
                .filter(r -> r != null).findFirst()
                .orElseGet(() -> {
                    String errLog = String.format("exception occured when invoking %s, params: %s", request.getRequestURI(),
                            request.getParameterMap().entrySet().stream().map(kv -> String.join("=", kv.getKey(), Arrays.toString(kv.getValue())))
                                    .collect(Collectors.joining(",")));
                    logger.error(errLog, ex);
                    return new ApiResult<>(INTERNAL_ERROR.getCode(), ex.getMessage());
                });
    }

    private static class Handler<E extends Exception> {
        private BiFunction<HttpServletRequest, E, ApiResult> f;
        private Class<? extends Exception> exceptionType;

        Handler(BiFunction<HttpServletRequest, E, ApiResult> f, Class<? extends E> exceptionType) {
            this.f = f;
            this.exceptionType = exceptionType;
        }

        @SuppressWarnings("unchecked")
        ApiResult handle(HttpServletRequest request, Exception e) {
            if (e.getClass() != exceptionType) {
                return null;
            }
            logger.warn("exception occurred: " + e);
            return f.apply(request, (E) e);
        }
    }

}