package com.fqz.sso.client.model;

/**
 * @author fuqianzhong
 * @date 19/1/8
 */
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public Result(){
        this.code = 200;
        this.message = "请求成功";
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
