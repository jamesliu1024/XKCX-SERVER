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

import static seig.ljm.xkckserver.common.constant.EnumConstant.Visitor.Role.ADMIN;
import static seig.ljm.xkckserver.common.constant.EnumConstant.Visitor.Role.VISITOR;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户接口", description = "处理普通用户的功能请求")
// @RequireRole(value = VISITOR)
public class UserController {

    private final VisitorService visitorService;
    private final ReservationService reservationService;
    private final AccessLogService accessLogService;
    private final RfidCardRecordService rfidCardRecordService;

    // 1. 个人信息管理
    @GetMapping("/profile")
    @Operation(summary = "获取个人信息")
    public ApiResult<Visitor> getProfile(
            @Parameter(description = "访客ID") @RequestParam Integer visitorId) {
        return ApiResult.success(visitorService.getByVisitorId(visitorId));
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人信息")
    public ApiResult<Visitor> updateProfile(
            @Parameter(description = "访客信息") @RequestBody Visitor visitor) {
        return ApiResult.success(visitorService.updateVisitor(visitor));
    }

    // 2. 预约管理
    @PostMapping("/reservation")
    @Operation(summary = "创建预约")
    public ApiResult<Reservation> createReservation(
            @Parameter(description = "预约信息") @RequestBody Reservation reservation) {
        return ApiResult.success(reservationService.createReservation(reservation));
    }

    @GetMapping("/reservations")
    @Operation(summary = "获取个人预约列表")
    public ApiResult<IPage<Reservation>> getReservations(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "访客ID") @RequestParam Integer visitorId,
            @Parameter(description = "预约状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ZonedDateTime startTime = startDate != null ? startDate.atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        ZonedDateTime endTime = endDate != null ? endDate.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        return ApiResult.success(reservationService.getReservationPage(current, size, visitorId, startTime, endTime, status));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "获取预约详情")
    public ApiResult<Reservation> getReservationDetail(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(reservationService.getById(reservationId));
    }

    @PutMapping("/reservation/{reservationId}")
    @Operation(summary = "更新预约信息")
    public ApiResult<Reservation> updateReservation(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId,
            @Parameter(description = "预约信息") @RequestBody Reservation reservation) {
        reservation.setReservationId(reservationId);
        return ApiResult.success(reservationService.updateReservation(reservation));
    }

    @DeleteMapping("/reservation/{reservationId}")
    @Operation(summary = "取消预约")
    public ApiResult<Boolean> cancelReservation(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(reservationService.deleteReservation(reservationId));
    }

    // 3. 门禁记录查询
    @GetMapping("/reservation/{reservationId}/access-logs")
    @Operation(summary = "获取指定预约的门禁记录")
    public ApiResult<IPage<AccessLog>> getReservationAccessLogs(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "访问类型") @RequestParam(required = false) String accessType,
            @Parameter(description = "访问结果") @RequestParam(required = false) String result) {
        return ApiResult.success(accessLogService.getLogPage(current, size, null, null, reservationId, 
            accessType, result, null, null));
    }

    @GetMapping("/access-logs")
    @Operation(summary = "获取个人门禁记录")
    public ApiResult<IPage<AccessLog>> getAccessLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "访客ID") @RequestParam Integer visitorId,
            @Parameter(description = "设备ID") @RequestParam(required = false) Integer deviceId,
            @Parameter(description = "访问类型") @RequestParam(required = false) String accessType,
            @Parameter(description = "访问结果") @RequestParam(required = false) String result,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ZonedDateTime startTime = startDate != null ? startDate.atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        ZonedDateTime endTime = endDate != null ? endDate.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        return ApiResult.success(accessLogService.getLogPage(current, size, deviceId, visitorId, null, 
            accessType, result, startTime, endTime));
    }

    // 4. RFID卡片记录
    @GetMapping("/card-records")
    @Operation(summary = "获取个人RFID卡片使用记录")
    public ApiResult<IPage<RfidCardRecord>> getCardRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "卡片ID") @RequestParam(required = false) Integer cardId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ZonedDateTime startTime = startDate != null ? startDate.atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        ZonedDateTime endTime = endDate != null ? endDate.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        return ApiResult.success(rfidCardRecordService.getRecordPage(current, size, cardId, operationType, startTime, endTime));
    }

    @GetMapping("/card-record/latest")
    @Operation(summary = "获取最新的RFID卡片记录")
    public ApiResult<RfidCardRecord> getLatestCardRecord(
            @Parameter(description = "卡片ID") @RequestParam Integer cardId) {
        return ApiResult.success(rfidCardRecordService.getLatestCardRecord(cardId));
    }
} 