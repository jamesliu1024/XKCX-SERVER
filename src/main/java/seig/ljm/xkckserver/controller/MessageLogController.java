package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.entity.MessageLog;
import seig.ljm.xkckserver.service.MessageLogService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messageLog")
@Tag(name = "MessageLog", description = "设备通信日志相")
public class MessageLogController {

    @Autowired
    private MessageLogService messageLogService;

    @PostMapping("/save")
    @Operation(summary = "保存设备通信日志")
    public ApiResult<Boolean> saveMessageLog(@RequestBody MessageLog messageLog) {
        return ApiResult.success(messageLogService.save(messageLog));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询通信日志")
    public ApiResult<Page<MessageLog>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer deviceId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Page<MessageLog> page = new Page<>(current, size);
        QueryWrapper<MessageLog> queryWrapper = new QueryWrapper<>();
        
        if (deviceId != null) {
            queryWrapper.eq("device_id", deviceId);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (startTime != null && endTime != null) {
            queryWrapper.between("receive_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("receive_time");
        
        return ApiResult.success(messageLogService.page(page, queryWrapper));
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "根据ID查询通信日志详情")
    public ApiResult<MessageLog> getById(@PathVariable Integer messageId) {
        return ApiResult.success(messageLogService.getById(messageId));
    }

    @GetMapping("/device/{deviceId}")
    @Operation(summary = "查询指定设备的最近通信记录")
    public ApiResult<List<MessageLog>> getRecentByDeviceId(
            @PathVariable Integer deviceId,
            @RequestParam(defaultValue = "10") Integer limit) {
        QueryWrapper<MessageLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("device_id", deviceId)
                   .orderByDesc("receive_time")
                   .last("LIMIT " + limit);
        return ApiResult.success(messageLogService.list(queryWrapper));
    }

    @GetMapping("/stats/daily")
    @Operation(summary = "获取每日通信统计")
    public ApiResult<List<Map<String, Object>>> getDailyStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> stats = messageLogService.getDailyStats(startDate, endDate);
        return ApiResult.success(stats);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除通信日志")
    public ApiResult<Boolean> delete(@PathVariable Integer messageId) {
        return ApiResult.success(messageLogService.removeById(messageId));
    }

    @GetMapping("/error-logs")
    @Operation(summary = "查询异常通信日志")
    public ApiResult<List<MessageLog>> getErrorLogs(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        QueryWrapper<MessageLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("status", "processed");
        
        if (startTime != null && endTime != null) {
            queryWrapper.between("receive_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("receive_time");
        return ApiResult.success(messageLogService.list(queryWrapper));
    }
}
