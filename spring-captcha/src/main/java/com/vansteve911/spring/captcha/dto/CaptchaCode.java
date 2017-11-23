package com.vansteve911.spring.captcha.dto;

/**
 * Created by vansteve911
 */
public class CaptchaCode {
    protected String key;
    protected String value;
    protected Long createTime;

    public CaptchaCode() {
    }

    public CaptchaCode(String key, String value, Long createTime) {
        this.key = key;
        this.value = value;
        this.createTime = createTime;
    }

    public CaptchaCode(CaptchaCode code) {
        this.key = code.key;
        this.value = code.value;
        this.createTime = code.createTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CaptchaCode)) return false;

        CaptchaCode that = (CaptchaCode) o;

        if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;
        if (getValue() != null ? !getValue().equals(that.getValue()) : that.getValue() != null) return false;
        return getCreateTime() != null ? getCreateTime().equals(that.getCreateTime()) : that.getCreateTime() == null;

    }

    @Override
    public int hashCode() {
        int result = getKey() != null ? getKey().hashCode() : 0;
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        result = 31 * result + (getCreateTime() != null ? getCreateTime().hashCode() : 0);
        return result;
    }
}
