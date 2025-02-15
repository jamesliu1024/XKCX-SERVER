package seig.ljm.xkckserver.common.api;

import lombok.Data;

@Data
public class ApiResult<T> {
    private Integer code;
    private String message;
    private T data;

    private ApiResult() {
    }

    private ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "操作成功", null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data);
    }

    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }

    public static <T> ApiResult<T> failed() {
        return new ApiResult<>(500, "操作失败", null);
    }

    public static <T> ApiResult<T> failed(String message) {
        return new ApiResult<>(500, message, null);
    }

    public static <T> ApiResult<T> failed(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }
}
