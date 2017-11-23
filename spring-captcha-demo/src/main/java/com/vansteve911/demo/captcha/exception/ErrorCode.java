package com.vansteve911.demo.captcha.exception;

/**
 * Created by vansteve911 on 2018/3/31.
 */
public enum ErrorCode {

    ERROR(-1, "ERROR"),
    SUCCESS(1, "OK"),
    FAILED(0, "FAILED"),

    ILLEGAL_PARAM(400, "ILLEGAL PARAMS"),
    UNAUTHORIZED(401,"UNAUTHORIZED"),
    FORBIDDEN(403,"OPERATION NOT PERMITTED"),
    NOT_FOUND(404, "NOT EXISTS"),
    METHOD_NOT_ALLOWED(405, "METHOD NOT ALLOWED"),
    CONFLICT(409, "RESOURCE CONFLICTED"),

    REDIRECT(302, "REDIRECTING"),

    INTERNAL_ERROR(500, "INTERNAL ERROR");

    private int code = 0;

    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}