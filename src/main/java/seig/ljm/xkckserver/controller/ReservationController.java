package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 预约管理控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/reservation")
@Tag(name = "Reservation", description = "预约记录")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "创建预约", description = "创建新的预约记录")
    public ApiResult<Reservation> createReservation(@RequestBody Reservation reservation) {
        return ApiResult.success(reservationService.createReservation(reservation));
    }

    @PutMapping("/{reservationId}")
    @Operation(summary = "更新预约", description = "更新指定预约记录的信息")
    public ApiResult<Reservation> updateReservation(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId,
            @RequestBody Reservation reservation) {
        reservation.setReservationId(reservationId);
        return ApiResult.success(reservationService.updateReservation(reservation));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询预约", description = "支持多条件分页查询预约记录")
    public ApiResult<IPage<Reservation>> getReservationPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "访客ID") @RequestParam(required = false) Integer visitorId,
            @Parameter(description = "开始时间（格式：yyyy-MM-dd）") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束时间（格式：yyyy-MM-dd）") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "预约状态") @RequestParam(required = false) String status) {
        ZonedDateTime startTime = startDate != null ? startDate.atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        ZonedDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX).atZone(TimeZoneConstant.ZONE_ID) : null;
        return ApiResult.success(reservationService.getReservationPage(current, size, visitorId, startTime, endTime, status));
    }

    @GetMapping("/visitor/{visitorId}")
    @Operation(summary = "获取访客预约", description = "获取指定访客的所有预约记录")
    public ApiResult<List<Reservation>> getVisitorReservations(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId) {
        return ApiResult.success(reservationService.getVisitorReservations(visitorId));
    }

    @GetMapping("/time-range")
    @Operation(summary = "获取时间范围预约", description = "获取指定时间范围内的预约记录")
    public ApiResult<List<Reservation>> getTimeRangeReservations(
            @Parameter(description = "开始时间（格式：yyyy-MM-dd）") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束时间（格式：yyyy-MM-dd）") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        ZonedDateTime startTime = startDate.atStartOfDay(TimeZoneConstant.ZONE_ID);
        ZonedDateTime endTime = endDate.atTime(LocalTime.MAX).atZone(TimeZoneConstant.ZONE_ID);
        return ApiResult.success(reservationService.getTimeRangeReservations(startTime, endTime));
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待确认预约", description = "获取所有待确认的预约记录")
    public ApiResult<List<Reservation>> getPendingReservations() {
        return ApiResult.success(reservationService.getPendingReservations());
    }

    @PutMapping("/{reservationId}/status")
    @Operation(summary = "更新预约状态", description = "更新指定预约记录的状态")
    public ApiResult<Boolean> updateReservationStatus(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId,
            @Parameter(description = "新状态") @RequestParam String status) {
        return ApiResult.success(reservationService.updateReservationStatus(reservationId, status));
    }

    @PutMapping("/{reservationId}/host-confirm")
    @Operation(summary = "更新接待人确认状态", description = "更新指定预约记录的主人确认状态")
    public ApiResult<Boolean> updateHostConfirm(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId,
            @Parameter(description = "确认状态") @RequestParam String hostConfirm) {
        return ApiResult.success(reservationService.updateHostConfirm(reservationId, hostConfirm));
    }

    @DeleteMapping("/{reservationId}")
    @Operation(summary = "删除预约", description = "软删除指定的预约记录")
    public ApiResult<Boolean> deleteReservation(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(reservationService.deleteReservation(reservationId));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "获取部门预约", description = "获取指定部门的预约记录")
    public ApiResult<List<Reservation>> getDepartmentReservations(
            @Parameter(description = "部门名称") @PathVariable String department) {
        return ApiResult.success(reservationService.getDepartmentReservations(department));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "获取即将开始的预约", description = "获取即将在一小时内开始的预约记录")
    public ApiResult<List<Reservation>> getUpcomingReservations() {
        return ApiResult.success(reservationService.getUpcomingReservations());
    }

    @GetMapping("/check-time")
    @Operation(summary = "检查时间可用性", description = "检查指定时间段是否可以预约")
    public ApiResult<Boolean> checkTimeAvailable(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(reservationService.checkTimeAvailable(startTime, endTime));
    }

    @GetMapping("/admin/page")
    @Operation(summary = "管理员分页查询", description = "管理员分页查询所有预约（包括隐藏的）")
    public ApiResult<IPage<Reservation>> getAdminReservationPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "访客ID") @RequestParam(required = false) Integer visitorId,
            @Parameter(description = "开始时间（格式：yyyy-MM-dd）") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束时间（格式：yyyy-MM-dd）") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        ZonedDateTime startTime = startDate != null ? startDate.atStartOfDay(TimeZoneConstant.ZONE_ID) : null;
        ZonedDateTime endTime = endDate != null ? endDate.atTime(LocalTime.MAX).atZone(TimeZoneConstant.ZONE_ID) : null;
        return ApiResult.success(reservationService.getAdminReservationPage(current, size, visitorId, startTime, endTime, status));
    }

    @GetMapping("/admin/visitor/{visitorId}")
    @Operation(summary = "管理员查询访客预约", description = "管理员查询访客的所有预约（包括隐藏的）")
    public ApiResult<List<Reservation>> getAdminVisitorReservations(
            @Parameter(description = "访客ID") @PathVariable Integer visitorId) {
        return ApiResult.success(reservationService.getAdminVisitorReservations(visitorId));
    }

    @GetMapping("/admin/time-range")
    @Operation(summary = "管理员查询时间范围预约", description = "管理员查询时间范围内的所有预约（包括隐藏的）")
    public ApiResult<List<Reservation>> getAdminTimeRangeReservations(
            @Parameter(description = "开始时间（格式：yyyy-MM-dd）") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束时间（格式：yyyy-MM-dd）") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        // 将 LocalDate 转换为当天的开始和结束时间
        ZonedDateTime startTime = startDate.atStartOfDay(TimeZoneConstant.ZONE_ID);
        ZonedDateTime endTime = endDate.atTime(LocalTime.MAX).atZone(TimeZoneConstant.ZONE_ID);
        return ApiResult.success(reservationService.getAdminTimeRangeReservations(startTime, endTime));
    }

    @GetMapping("/admin/department/{department}")
    @Operation(summary = "管理员查询部门预约", description = "管理员查询部门的所有预约（包括隐藏的）")
    public ApiResult<List<Reservation>> getAdminDepartmentReservations(
            @Parameter(description = "部门名称") @PathVariable String department) {
        return ApiResult.success(reservationService.getAdminDepartmentReservations(department));
    }

    @PutMapping("/admin/{reservationId}/restore")
    @Operation(summary = "恢复隐藏预约", description = "管理员恢复被隐藏的预约记录")
    public ApiResult<Boolean> restoreReservation(
            @Parameter(description = "预约ID") @PathVariable Integer reservationId) {
        return ApiResult.success(reservationService.restoreReservation(reservationId));
    }
}
