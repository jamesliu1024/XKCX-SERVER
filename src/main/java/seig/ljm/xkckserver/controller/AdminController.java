package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.common.security.RequireRole;
import seig.ljm.xkckserver.entity.*;
import seig.ljm.xkckserver.service.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static seig.ljm.xkckserver.common.constant.EnumConstant.Visitor.Role.ADMIN;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "管理员接口", description = "处理管理员特有的功能请求")
@RequireRole(value = ADMIN)
public class AdminController {

    private final VisitorService visitorService;
    private final ReservationService reservationService;
    private final RfidCardService rfidCardService;
    private final RfidCardRecordService rfidCardRecordService;
    private final AccessDeviceService accessDeviceService;
    private final AccessLogService accessLogService;
    private final QuotaSettingService quotaSettingService;
    private final BlacklistRecordService blacklistRecordService;
    private final OperationLogService operationLogService;

    // 1. 用户管理相关接口
    @GetMapping("/visitors")
    @Operation(summary = "获取所有访客列表")
    public ApiResult<IPage<Visitor>> getAllVisitors(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "账号状态") @RequestParam(required = false) String accountStatus) {
        return ApiResult.success(visitorService.getVisitorPage(current, size, role, accountStatus));
    }

    @PutMapping("/visitor/{visitorId}/status")
    @Operation(summary = "更新访客账号状态")
    public ApiResult<Void> updateVisitorStatus(
            @PathVariable Integer visitorId,
            @RequestParam String status) {
        visitorService.updateAccountStatus(visitorId, status);
        return ApiResult.success();
    }

    @PostMapping("/blacklist")
    @Operation(summary = "将访客加入黑名单")
    public ApiResult<BlacklistRecord> addToBlacklist(
            @RequestBody BlacklistRecord blacklistRecord) {
        return ApiResult.success(blacklistRecordService.addToBlacklist(blacklistRecord));
    }

    // 2. 预约管理相关接口
    @GetMapping("/reservations")
    @Operation(summary = "获取所有预约列表")
    public ApiResult<IPage<Reservation>> getAllReservations(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "预约状态") @RequestParam(required = false) String status,
            @Parameter(description = "预约日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResult.success(reservationService.getAdminReservationPage(current, size, null, 
            date != null ? date.atStartOfDay(TimeZoneConstant.ZONE_ID) : null,
            date != null ? date.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID) : null, 
            status));
    }

    @PutMapping("/reservation/{reservationId}/status")
    @Operation(summary = "更新预约状态")
    public ApiResult<Void> updateReservationStatus(
            @PathVariable Integer reservationId,
            @RequestParam String status) {
        reservationService.updateStatus(reservationId, status);
        return ApiResult.success();
    }

    // 3. RFID卡片管理相关接口
    @GetMapping("/rfid-cards")
    @Operation(summary = "获取所有RFID卡片列表")
    public ApiResult<IPage<RfidCard>> getAllRfidCards(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "卡片状态") @RequestParam(required = false) String status) {
        return ApiResult.success(rfidCardService.getCardPage(current, size, status));
    }

    @PostMapping("/rfid-card/issue")
    @Operation(summary = "发放RFID卡片")
    public ApiResult<RfidCardRecord> issueRfidCard(@RequestBody Map<String, Object> request) {
        return ApiResult.success(rfidCardRecordService.issueCard(
                (Integer) request.get("cardId"),
                (Integer) request.get("reservationId"),
                (Integer) request.get("adminId"),
                (String) request.get("remarks")
        ));
    }

    @PostMapping("/rfid-card/return")
    @Operation(summary = "归还RFID卡片")
    public ApiResult<RfidCardRecord> returnRfidCard(@RequestBody Map<String, Object> request) {
        return ApiResult.success(rfidCardRecordService.returnCard(
                (Integer) request.get("cardId"),
                (Integer) request.get("adminId"),
                (String) request.get("remarks")
        ));
    }

    // 4. 门禁设备管理相关接口
    @GetMapping("/devices")
    @Operation(summary = "获取所有门禁设备列表")
    public ApiResult<IPage<AccessDevice>> getAllDevices(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "设备状态") @RequestParam(required = false) String status,
            @Parameter(description = "设备类型") @RequestParam(required = false) String type) {
        return ApiResult.success(accessDeviceService.getDevicePage(current, size, status, type));
    }

    @PutMapping("/device/{deviceId}/status")
    @Operation(summary = "更新设备状态")
    public ApiResult<Void> updateDeviceStatus(
            @PathVariable Integer deviceId,
            @RequestParam String status) {
        accessDeviceService.updateStatus(deviceId, status);
        return ApiResult.success();
    }

    // 5. 配额管理相关接口
    @PostMapping("/quota")
    @Operation(summary = "设置每日预约配额")
    public ApiResult<QuotaSetting> setDailyQuota(@RequestBody QuotaSetting quotaSetting) {
        return ApiResult.success(quotaSettingService.setQuota(quotaSetting));
    }

    @GetMapping("/quota")
    @Operation(summary = "获取配额设置列表")
    public ApiResult<IPage<QuotaSetting>> getQuotaSettings(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResult.success(quotaSettingService.getQuotaPage(current, size, startDate, endDate, null));
    }

    // 6. 数据统计相关接口
    @GetMapping("/statistics/access")
    @Operation(summary = "获取进出统计数据")
    public ApiResult<Map<String, Object>> getAccessStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResult.success(accessLogService.getStatistics(startDate, endDate));
    }

    @GetMapping("/statistics/device-usage")
    @Operation(summary = "获取设备使用统计")
    public ApiResult<Map<String, Object>> getDeviceUsageStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResult.success(accessLogService.getDeviceUsageStatistics(startDate, endDate));
    }

    // 7. 操作日志查询接口
    @GetMapping("/operation-logs")
    @Operation(summary = "获取操作日志")
    public ApiResult<IPage<OperationLog>> getOperationLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "操作日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResult.success(operationLogService.getLogPage(current, size, null, operationType, null,
            date != null ? date.atStartOfDay(TimeZoneConstant.ZONE_ID) : null,
            date != null ? date.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID) : null));
    }
} 