package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.ApiResult;
import seig.ljm.xkckserver.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.RfidCardRecord;
import seig.ljm.xkckserver.service.RfidCardRecordService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * RFID卡片使用记录控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/rfid-card-record")
@Tag(name = "RfidCardRecord", description = "RFID卡片使用记录")
@RequiredArgsConstructor
public class RfidCardRecordController {

    private final RfidCardRecordService rfidCardRecordService;

    @PostMapping("/issue")
    @Operation(summary = "记录发卡", description = "记录RFID卡片的发放操作")
    public ApiResult<RfidCardRecord> recordIssue(
            @Parameter(description = "卡片ID") @RequestParam Integer cardId,
            @Parameter(description = "预约ID") @RequestParam Integer reservationId,
            @Parameter(description = "管理员ID") @RequestParam Integer adminId,
            @Parameter(description = "过期时间") @RequestParam @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime expirationTime,
            @Parameter(description = "备注") @RequestParam(required = false) String remarks) {
        return ApiResult.success(rfidCardRecordService.recordIssue(cardId, reservationId, adminId, expirationTime, remarks));
    }

    @PostMapping("/return")
    @Operation(summary = "记录还卡", description = "记录RFID卡片的归还操作")
    public ApiResult<RfidCardRecord> recordReturn(
            @Parameter(description = "卡片ID") @RequestParam Integer cardId,
            @Parameter(description = "预约ID") @RequestParam Integer reservationId,
            @Parameter(description = "管理员ID") @RequestParam Integer adminId,
            @Parameter(description = "备注") @RequestParam(required = false) String remarks) {
        return ApiResult.success(rfidCardRecordService.recordReturn(cardId, reservationId, adminId, remarks));
    }

    @PostMapping("/lost")
    @Operation(summary = "记录挂失", description = "记录RFID卡片的挂失操作")
    public ApiResult<RfidCardRecord> recordLost(
            @Parameter(description = "卡片ID") @RequestParam Integer cardId,
            @Parameter(description = "预约ID") @RequestParam Integer reservationId,
            @Parameter(description = "管理员ID") @RequestParam Integer adminId,
            @Parameter(description = "备注") @RequestParam(required = false) String remarks) {
        return ApiResult.success(rfidCardRecordService.recordLost(cardId, reservationId, adminId, remarks));
    }

    @PostMapping("/deactivate")
    @Operation(summary = "记录注销", description = "记录RFID卡片的注销操作")
    public ApiResult<RfidCardRecord> recordDeactivate(
            @Parameter(description = "卡片ID") @RequestParam Integer cardId,
            @Parameter(description = "预约ID") @RequestParam Integer reservationId,
            @Parameter(description = "管理员ID") @RequestParam Integer adminId,
            @Parameter(description = "备注") @RequestParam(required = false) String remarks) {
        return ApiResult.success(rfidCardRecordService.recordDeactivate(cardId, reservationId, adminId, remarks));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询记录", description = "支持多条件分页查询RFID卡片使用记录")
    public ApiResult<IPage<RfidCardRecord>> getRecordPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "卡片ID") @RequestParam(required = false) Integer cardId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "开始时间（格式：yyyy-MM-dd）") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束时间（格式：yyyy-MM-dd）") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ZonedDateTime startTime = startDate != null ? startDate.atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        ZonedDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX).atZone(TimeZoneConstant.ZONE_ID) : null;
        return ApiResult.success(rfidCardRecordService.getRecordPage(current, size, cardId, operationType, startTime, endTime));
    }

    @GetMapping("/card/{cardId}")
    @Operation(summary = "获取卡片记录", description = "获取指定RFID卡片的使用记录")
    public ApiResult<List<RfidCardRecord>> getCardRecords(
            @Parameter(description = "卡片ID") @PathVariable Integer cardId) {
        return ApiResult.success(rfidCardRecordService.getCardRecords(cardId));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "获取预约记录", description = "获取指定预约的RFID卡片使用记录")
    public ApiResult<List<RfidCardRecord>> getReservationRecords(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(rfidCardRecordService.getReservationRecords(reservationId));
    }

    @GetMapping("/admin/{adminId}")
    @Operation(summary = "获取管理员记录", description = "获取指定管理员的RFID卡片操作记录")
    public ApiResult<List<RfidCardRecord>> getAdminRecords(
            @Parameter(description = "管理员ID") @PathVariable Integer adminId) {
        return ApiResult.success(rfidCardRecordService.getAdminRecords(adminId));
    }

    @GetMapping("/time-range")
    @Operation(summary = "获取时间范围记录", description = "获取指定时间范围内的RFID卡片使用记录")
    public ApiResult<List<RfidCardRecord>> getTimeRangeRecords(
            @Parameter(description = "开始时间（格式：yyyy-MM-dd）") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束时间（格式：yyyy-MM-dd）") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ZonedDateTime startTime = startDate.atStartOfDay(TimeZoneConstant.ZONE_ID);
        ZonedDateTime endTime = endDate.atTime(LocalTime.MAX).atZone(TimeZoneConstant.ZONE_ID);
        return ApiResult.success(rfidCardRecordService.getTimeRangeRecords(startTime, endTime));
    }

    @GetMapping("/latest/{cardId}")
    @Operation(summary = "获取最新记录", description = "获取指定RFID卡片的最新使用记录")
    public ApiResult<RfidCardRecord> getLatestCardRecord(
            @Parameter(description = "卡片ID") @PathVariable Integer cardId) {
        return ApiResult.success(rfidCardRecordService.getLatestCardRecord(cardId));
    }

    @DeleteMapping("/{recordId}")
    @Operation(summary = "删除记录", description = "软删除指定的RFID卡片使用记录")
    public ApiResult<Boolean> deleteRecord(
            @Parameter(description = "记录ID") @PathVariable Integer recordId) {
        return ApiResult.success(rfidCardRecordService.deleteRecord(recordId));
    }

    @GetMapping("/expiring")
    @Operation(summary = "获取即将过期记录", description = "获取即将过期的RFID卡片使用记录")
    public ApiResult<List<RfidCardRecord>> getExpiringRecords() {
        return ApiResult.success(rfidCardRecordService.getExpiringRecords());
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除记录", description = "批量软删除RFID卡片使用记录")
    public ApiResult<Boolean> batchDeleteRecords(
            @Parameter(description = "记录ID列表") @RequestParam List<Integer> recordIds) {
        return ApiResult.success(rfidCardRecordService.batchDeleteRecords(recordIds));
    }
}
