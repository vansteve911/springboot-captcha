package com.vansteve911.demo.captcha.exception;

/**
 * Created by vansteve911 on 2018/3/31.
 */
public class BusinessException extends RuntimeException {

    private int code;

    private String msg;

    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public BusinessException(int code, String msg, Object data) {
        super();
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BusinessException(ErrorCode errorCode, Object data) {
        this(errorCode.getCode(), errorCode.getMsg(), data);
    }

    public BusinessException(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMsg(), null);
    }

    public BusinessException(ErrorCode errorCode, String msg) {
        this(errorCode.getCode(), msg, null);
    }

    public BusinessException(int code, String msg) {
        this(code, msg, null);
    }

    @Override
    public String toString() {
        return "CaptchaException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}