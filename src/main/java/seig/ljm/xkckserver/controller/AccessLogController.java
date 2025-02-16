package seig.ljm.xkckserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.response.Result;
import seig.ljm.xkckserver.entity.AccessLog;
import seig.ljm.xkckserver.service.AccessLogService;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 进出记录控制器
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@Tag(name = "AccessLog", description = "进出记录")
@RestController
@RequestMapping("/accessLog")
public class AccessLogController {

    private AccessLogService accessLogService;
    @Autowired
    public AccessLogController(AccessLogService accessLogService) {
        this.accessLogService = accessLogService;
    }

    @Operation(summary = "查询进出记录")
    @GetMapping("/list")
    public Result<List<AccessLog>> list(
            @Parameter(description = "访客ID") @RequestParam(required = false) Integer visitorId,
            @Parameter(description = "设备ID") @RequestParam(required = false) Integer deviceId,
            @Parameter(description = "访问类型(entry/exit)") @RequestParam(required = false) String accessType,
            @Parameter(description = "访问结果(allowed/denied)") @RequestParam(required = false) String result,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        List<AccessLog> logs = accessLogService.queryLogs(visitorId, deviceId, accessType, result, startTime, endTime);
        return Result.success(logs);
    }

    @Operation(summary = "获取单个进出记录")
    @GetMapping("/{id}")
    public Result<AccessLog> getById(@PathVariable Integer id) {
        return Result.success(accessLogService.getById(id));
    }

    @Operation(summary = "获取设备进出统计")
    @GetMapping("/stats/device/{deviceId}")
    public Result<Map<String, Object>> getDeviceStats(
            @PathVariable Integer deviceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Map<String, Object> stats = accessLogService.getDeviceStats(deviceId, startTime, endTime);
        return Result.success(stats);
    }

    @Operation(summary = "获取每日进出统计")
    @GetMapping("/stats/daily")
    public Result<List<Map<String, Object>>> getDailyStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        List<Map<String, Object>> stats = accessLogService.getDailyStats(startDate, endDate);
        return Result.success(stats);
    }

    @Operation(summary = "获取访问高峰时段")
    @GetMapping("/stats/peak-hours")
    public Result<List<Map<String, Object>>> getPeakHours(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        List<Map<String, Object>> peakHours = accessLogService.getPeakHours(date);
        return Result.success(peakHours);
    }

    @Operation(summary = "获取异常访问记录")
    @GetMapping("/abnormal")
    public Result<List<AccessLog>> getAbnormalLogs(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        List<AccessLog> abnormalLogs = accessLogService.getAbnormalLogs(startTime, endTime);
        return Result.success(abnormalLogs);
    }
}
