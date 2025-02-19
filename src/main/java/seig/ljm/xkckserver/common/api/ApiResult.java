package seig.ljm.xkckserver.common.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * API统一返回结果
 *
 * @author ljm
 * @since 2025-02-18
 */
@Data
@Schema(description = "统一API返回结果")
public class ApiResult<T> {
    
    @Schema(description = "状态码")
    private Integer code;
    
    @Schema(description = "返回消息")
    private String message;
    
    @Schema(description = "返回数据")
    private T data;
    
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
    
    public static <T> ApiResult<T> error() {
        return new ApiResult<>(500, "操作失败", null);
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(500, message, null);
    }
    
    public static <T> ApiResult<T> error(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }
} 