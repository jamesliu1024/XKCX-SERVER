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
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import seig.ljm.xkckserver.common.utils.RedisUtil;
import seig.ljm.xkckserver.mqtt.MQTTGateway;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static seig.ljm.xkckserver.common.constant.EnumConstant.Visitor.Role.ADMIN;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "管理员接口", description = "处理管理员特有的功能请求")
// @RequireRole(value = ADMIN)
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
    private final RedisUtil redisUtil;
    private final MQTTGateway mqttGateway;
    private final ObjectMapper objectMapper;

    // 1. 用户管理相关接口
    @GetMapping("/visitor/{visitorId}")
    @Operation(summary = "获取访客详情信息")
    public ApiResult<Map<String, Object>> getVisitorDetail(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId,
            @Parameter(description = "是否包含预约历史") @RequestParam(defaultValue = "false") Boolean includeReservations,
            @Parameter(description = "是否包含门禁记录") @RequestParam(defaultValue = "false") Boolean includeAccessLogs) {
        return ApiResult.success(visitorService.getVisitorDetail(visitorId, includeReservations, includeAccessLogs));
    }

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

    @PutMapping("/visitor/{visitorId}")
    @Operation(summary = "修改访客基本信息和账号状态")
    public ApiResult<Visitor> updateVisitor(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId,
            @Parameter(description = "访客信息") @RequestBody Visitor visitor,
            @Parameter(description = "操作管理员ID") @RequestParam Integer adminId) {
        visitor.setVisitorId(visitorId);
        // 记录操作日志
        OperationLog log = new OperationLog();
        log.setOperatorId(adminId);
        log.setOperationType("UPDATE_VISITOR");
        log.setTargetId(visitorId);
        log.setDetails(JSON.toJSONString(visitor));
        operationLogService.save(log);
        
        return ApiResult.success(visitorService.updateVisitor(visitor));
    }

    // 2. 预约管理相关接口
    @PutMapping("/reservation/{reservationId}")
    @Operation(summary = "管理员修改预约信息")
    public ApiResult<Reservation> updateReservation(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId,
            @Parameter(description = "预约信息") @RequestBody Reservation reservation,
            @Parameter(description = "操作管理员ID") @RequestParam Integer adminId) {
        reservation.setReservationId(reservationId);
        return ApiResult.success(reservationService.updateAdminReservation(reservation, adminId));
    }

    @GetMapping("/reservations/pending/count")
    @Operation(summary = "获取待审核预约数量")
    public ApiResult<Map<String, Object>> getPendingReservationCount(
            @Parameter(description = "日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return ApiResult.success(reservationService.getPendingReservationCount(date));
    }

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

    @PutMapping("/reservation/status")
    @Operation(summary = "更新预约状态")
    public ApiResult<Void> updateReservationStatus(
            @Parameter(description = "管理员ID") @RequestParam Integer adminId,
            @Parameter(description = "更新状态请求") @RequestBody Map<String, String> body) {
        reservationService.updateStatus(
            Integer.parseInt(body.get("reservationId")), 
            body.get("newStatus"),
            adminId,
            body.get("remarks")
        );
        return ApiResult.success();
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "获取预约详情")
    public ApiResult<Reservation> getReservationDetail(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(reservationService.getById(reservationId));
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

    @GetMapping("/rfid-card/{cardId}")
    @Operation(summary = "获取卡片详细信息")
    public ApiResult<RfidCard> getCardDetail(
            @Parameter(description = "卡片ID") @PathVariable Integer cardId) {
        return ApiResult.success(rfidCardService.getById(cardId));
    }

    @GetMapping("/rfid-card/{cardId}/records")
    @Operation(summary = "获取卡片使用记录")
    public ApiResult<IPage<RfidCardRecord>> getCardRecords(
            @Parameter(description = "卡片ID") @PathVariable Integer cardId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ZonedDateTime startTime = startDate != null ? startDate.atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        ZonedDateTime endTime = endDate != null ? endDate.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        return ApiResult.success(rfidCardRecordService.getRecordPage(current, size, cardId, operationType, startTime, endTime));
    }

    @PostMapping("/rfid-card")
    @Operation(summary = "添加新RFID卡片")
    public ApiResult<RfidCard> addCard(
            @Parameter(description = "卡片信息") @RequestBody RfidCard card) {
        return ApiResult.success(rfidCardService.addCard(card));
    }

    @PutMapping("/rfid-card/{cardId}")
    @Operation(summary = "更新RFID卡片信息")
    public ApiResult<RfidCard> updateCard(
            @Parameter(description = "卡片ID") @PathVariable Integer cardId,
            @Parameter(description = "卡片信息") @RequestBody RfidCard card) {
        card.setCardId(cardId);
        return ApiResult.success(rfidCardService.updateCard(card));
    }

    @PostMapping("/rfid-card/issue")
    @Operation(summary = "发放RFID卡片")
    public ApiResult<RfidCardRecord> issueRfidCard(
            @Parameter(description = "发卡信息") @RequestBody RfidCardRecord record) {
        return ApiResult.success(rfidCardRecordService.issueCard(
                record.getCardId(),
                record.getReservationId(),
                record.getAdminId(),
                record.getRemarks()
        ));
    }

    @PostMapping("/rfid-card/return")
    @Operation(summary = "归还RFID卡片")
    public ApiResult<RfidCardRecord> returnRfidCard(
            @Parameter(description = "卡片ID") @RequestParam Integer cardId,
            @Parameter(description = "管理员ID") @RequestParam Integer adminId,
            @Parameter(description = "备注") @RequestParam(required = false) String remarks) {
        return ApiResult.success(rfidCardRecordService.returnCard(cardId, adminId, remarks));
    }

    // 4. 门禁设备管理相关接口
    @GetMapping("/device/{deviceId}")
    @Operation(summary = "获取设备详情")
    public ApiResult<AccessDevice> getDeviceDetail(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId) {
        return ApiResult.success(accessDeviceService.getDeviceStatus(deviceId));
    }

    @PostMapping("/device")
    @Operation(summary = "添加新设备")
    public ApiResult<AccessDevice> addDevice(
            @Parameter(description = "设备信息") @RequestBody AccessDevice device) {
        return ApiResult.success(accessDeviceService.addDevice(device));
    }

    @PutMapping("/device/{deviceId}")
    @Operation(summary = "更新设备信息")
    public ApiResult<AccessDevice> updateDevice(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId,
            @Parameter(description = "设备信息") @RequestBody AccessDevice device) {
        device.setDeviceId(deviceId);
        return ApiResult.success(accessDeviceService.updateDevice(device));
    }

    @GetMapping("/devices")
    @Operation(summary = "获取门禁设备分页列表")
    public ApiResult<IPage<AccessDevice>> getDevices(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "设备状态(online/offline/maintenance)") @RequestParam(required = false) String status,
            @Parameter(description = "设备类型(campus_gate/facility_gate/management)") @RequestParam(required = false) String type,
            @Parameter(description = "设备位置") @RequestParam(required = false) String location,
            @Parameter(description = "门禁状态(open/closed)") @RequestParam(required = false) String doorStatus,
            @Parameter(description = "排序字段(lastHeartbeatTime/status)") @RequestParam(defaultValue = "lastHeartbeatTime") String sortField,
            @Parameter(description = "排序方式(asc/desc)") @RequestParam(defaultValue = "desc") String sortOrder) {
        return ApiResult.success(accessDeviceService.getDevicePage(current, size, status, type, location, doorStatus, sortField, sortOrder));
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

    @GetMapping("/quota/{quotaId}")
    @Operation(summary = "获取指定配额设置详情")
    public ApiResult<QuotaSetting> getQuotaDetail(
            @Parameter(description = "配额ID") @PathVariable Integer quotaId) {
        return ApiResult.success(quotaSettingService.getById(quotaId));
    }

    @PutMapping("/quota/{quotaId}")
    @Operation(summary = "修改配额设置")
    public ApiResult<QuotaSetting> updateQuotaSetting(
            @Parameter(description = "配额ID") @PathVariable Integer quotaId,
            @Parameter(description = "配额设置信息") @RequestBody QuotaSetting quotaSetting,
            @Parameter(description = "操作管理员ID") @RequestParam Integer adminId) {
        quotaSetting.setQuotaId(quotaId);
        
        // 记录操作日志
        OperationLog log = new OperationLog();
        log.setOperatorId(adminId);
        log.setOperationType("UPDATE_QUOTA");
        log.setTargetId(quotaId);
        log.setDetails(JSON.toJSONString(quotaSetting));
        operationLogService.save(log);
        
        return ApiResult.success(quotaSettingService.setQuota(quotaSetting));
    }

    // 6. 数据统计相关接口
    @GetMapping("/statistics/reservations")
    @Operation(summary = "获取预约统计数据")
    public ApiResult<Map<String, Object>> getReservationStatistics(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ApiResult.success(reservationService.getReservationStatistics(startDate, endDate));
    }

    @GetMapping("/statistics/device-status")
    @Operation(summary = "获取设备运行状态统计")
    public ApiResult<Map<String, Object>> getDeviceStatusStatistics() {
        return ApiResult.success(accessDeviceService.getDeviceStatusStatistics());
    }

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

    @GetMapping("/statistics/real-time-flow")
    @Operation(summary = "获取实时校园人流量")
    public ApiResult<Map<String, Object>> getRealTimeFlow() {
        return ApiResult.success(accessLogService.getRealTimeFlow());
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