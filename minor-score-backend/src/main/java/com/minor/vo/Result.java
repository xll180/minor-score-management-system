package com.minor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类
 * 用于封装所有API接口的返回数据，保持响应格式统一
 *
 * @param <T> 响应数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 状态码: 200表示成功，其他表示异常
     */
    private int code;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 操作成功（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 操作成功（仅消息）
     *
     * @param msg 成功消息
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(200, msg, null);
    }

    /**
     * 操作失败（自定义状态码和消息）
     *
     * @param code 错误状态码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 操作失败（默认500状态码）
     *
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}