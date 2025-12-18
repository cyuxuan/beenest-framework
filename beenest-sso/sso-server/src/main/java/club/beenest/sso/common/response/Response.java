package club.beenest.sso.common.response;

import lombok.Data;
import java.io.Serializable;

/**
 * 通用响应结构体
 *
 * @param <T> 数据类型
 */
@Data
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码 (200: 成功, 其他: 失败)
     */
    private Integer code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public Response() {
    }

    public Response(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "操作成功", data);
    }

    /**
     * 成功响应 (自定义消息)
     *
     * @param msg  消息
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> Response<T> success(String msg, T data) {
        return new Response<>(200, msg, data);
    }

    /**
     * 失败响应
     *
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> Response<T> error(String msg) {
        return new Response<>(500, msg, null);
    }

    /**
     * 失败响应 (自定义状态码)
     *
     * @param code 状态码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return 响应对象
     */
    public static <T> Response<T> error(Integer code, String msg) {
        return new Response<>(code, msg, null);
    }
}
