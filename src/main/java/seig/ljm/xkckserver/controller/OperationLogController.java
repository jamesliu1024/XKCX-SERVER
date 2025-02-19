package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.OperationLog;
import seig.ljm.xkckserver.service.OperationLogService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 操作日志控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/operationLog")
@Tag(name = "OperationLog", description = "操作日志")
public class OperationLogController {

    private OperationLogService operationLogService;
    @Autowired
    public OperationLogController(OperationLogService operationLogService){
        this.operationLogService = operationLogService;
    }

    @GetMapping("/{logId}")
    @Operation(summary = "获取日志详情", description = "根据日志ID获取详细信息")
    public ApiResult<OperationLog> getLog(
            @Parameter(description = "日志ID") @PathVariable Integer logId) {
        return ApiResult.success(operationLogService.getById(logId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询日志", description = "支持多条件分页查询操作日志")
    public ApiResult<IPage<OperationLog>> getLogPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "操作员ID") @RequestParam(required = false) Integer operatorId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "操作对象ID") @RequestParam(required = false) Integer targetId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(operationLogService.getLogPage(current, size, operatorId, operationType, targetId, startTime, endTime));
    }

    @GetMapping("/operator/{operatorId}")
    @Operation(summary = "获取操作员日志", description = "获取指定操作员的操作日志")
    public ApiResult<List<OperationLog>> getOperatorLogs(
            @Parameter(description = "操作员ID") @PathVariable Integer operatorId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(operationLogService.getOperatorLogs(operatorId, startTime, endTime));
    }

    @GetMapping("/type/{operationType}")
    @Operation(summary = "获取操作类型日志", description = "获取指定操作类型的日志")
    public ApiResult<List<OperationLog>> getOperationTypeLogs(
            @Parameter(description = "操作类型") @PathVariable String operationType,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(operationLogService.getOperationTypeLogs(operationType, startTime, endTime));
    }

    @GetMapping("/target/{targetId}")
    @Operation(summary = "获取目标对象日志", description = "获取指定目标对象的操作日志")
    public ApiResult<List<OperationLog>> getTargetLogs(
            @Parameter(description = "目标对象ID") @PathVariable Integer targetId) {
        return ApiResult.success(operationLogService.getTargetLogs(targetId));
    }

    @PostMapping("/record")
    @Operation(summary = "记录操作", description = "记录一条操作日志")
    public ApiResult<OperationLog> recordOperation(@RequestBody OperationLog log) {
        return ApiResult.success(operationLogService.recordOperation(log));
    }
}
