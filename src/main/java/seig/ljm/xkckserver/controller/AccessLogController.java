package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.ApiResult;
import seig.ljm.xkckserver.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.AccessLog;
import seig.ljm.xkckserver.service.AccessLogService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 门禁日志控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/accessLog")
@Tag(name = "AccessLog", description = "门禁日志")
public class AccessLogController {

    private AccessLogService accessLogService;
    @Autowired
    public AccessLogController(AccessLogService accessLogService){
        this.accessLogService = accessLogService;
    }

    @GetMapping("/{logId}")
    @Operation(summary = "获取日志详情", description = "根据日志ID获取详细信息")
    public ApiResult<AccessLog> getLog(
            @Parameter(description = "日志ID") @PathVariable Integer logId) {
        return ApiResult.success(accessLogService.getById(logId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询日志", description = "支持多条件分页查询日志")
    public ApiResult<IPage<AccessLog>> getLogPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "设备ID") @RequestParam(required = false) Integer deviceId,
            @Parameter(description = "访客ID") @RequestParam(required = false) Integer visitorId,
            @Parameter(description = "预约ID") @RequestParam(required = false) Integer reservationId,
            @Parameter(description = "访问类型") @RequestParam(required = false) String accessType,
            @Parameter(description = "访问结果") @RequestParam(required = false) String result,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(accessLogService.getLogPage(current, size, deviceId, visitorId, reservationId, accessType, result, startTime, endTime));
    }

    @GetMapping("/device/{deviceId}")
    @Operation(summary = "获取设备日志", description = "获取指定设备的访问日志")
    public ApiResult<List<AccessLog>> getDeviceLogs(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(accessLogService.getDeviceLogs(deviceId, startTime, endTime));
    }

    @GetMapping("/visitor/{visitorId}")
    @Operation(summary = "获取访客日志", description = "获取指定访客的访问日志")
    public ApiResult<List<AccessLog>> getVisitorLogs(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(accessLogService.getVisitorLogs(visitorId, startTime, endTime));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "获取预约日志", description = "获取指定预约的访问日志")
    public ApiResult<List<AccessLog>> getReservationLogs(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(accessLogService.getReservationLogs(reservationId));
    }

    @PostMapping("/record")
    @Operation(summary = "记录访问日志", description = "记录一次门禁访问")
    public ApiResult<AccessLog> recordAccess(@RequestBody AccessLog log) {
        return ApiResult.success(accessLogService.recordAccess(log));
    }

    @PutMapping("/{logId}/hide")
    @Operation(summary = "隐藏日志", description = "将指定日志标记为隐藏")
    public ApiResult<Boolean> hideLog(
            @Parameter(description = "日志ID") @PathVariable Integer logId) {
        return ApiResult.success(accessLogService.hideLog(logId));
    }

    @PutMapping("/{logId}/restore")
    @Operation(summary = "恢复日志", description = "将指定日志恢复为可见")
    public ApiResult<Boolean> restoreLog(
            @Parameter(description = "日志ID") @PathVariable Integer logId) {
        return ApiResult.success(accessLogService.restoreLog(logId));
    }

    @GetMapping("/all/page")
    @Operation(summary = "分页查询所有日志", description = "支持多条件分页查询所有日志（包括隐藏日志）")
    public ApiResult<IPage<AccessLog>> getAllLogPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "设备ID") @RequestParam(required = false) Integer deviceId,
            @Parameter(description = "访客ID") @RequestParam(required = false) Integer visitorId,
            @Parameter(description = "预约ID") @RequestParam(required = false) Integer reservationId,
            @Parameter(description = "访问类型") @RequestParam(required = false) String accessType,
            @Parameter(description = "访问结果") @RequestParam(required = false) String result,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(accessLogService.getAllLogPage(current, size, deviceId, visitorId, reservationId, accessType, result, startTime, endTime));
    }

    @GetMapping("/all/device/{deviceId}")
    @Operation(summary = "获取设备所有日志", description = "获取指定设备的所有访问日志（包括隐藏日志）")
    public ApiResult<List<AccessLog>> getAllDeviceLogs(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(accessLogService.getAllDeviceLogs(deviceId, startTime, endTime));
    }

    @GetMapping("/all/visitor/{visitorId}")
    @Operation(summary = "获取访客所有日志", description = "获取指定访客的所有访问日志（包括隐藏日志）")
    public ApiResult<List<AccessLog>> getAllVisitorLogs(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(accessLogService.getAllVisitorLogs(visitorId, startTime, endTime));
    }

    @GetMapping("/all/reservation/{reservationId}")
    @Operation(summary = "获取预约所有日志", description = "获取指定预约的所有访问日志（包括隐藏日志）")
    public ApiResult<List<AccessLog>> getAllReservationLogs(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(accessLogService.getAllReservationLogs(reservationId));
    }
}
