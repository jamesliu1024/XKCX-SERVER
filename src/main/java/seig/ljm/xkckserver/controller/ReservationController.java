package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.service.ReservationService;
import seig.ljm.xkckserver.service.QuotaSettingService;

import java.time.LocalDateTime;

/**
 * <p>
 * 预约管理控制器
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@RestController
@RequestMapping("/reservation")
@Tag(name = "Reservation", description = "访客预约")
public class ReservationController {

    private ReservationService reservationService;
    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    private QuotaSettingService quotaSettingService;
    @Autowired
    public void setQuotaSettingService(QuotaSettingService quotaSettingService) {
        this.quotaSettingService = quotaSettingService;
    }

    @PostMapping
    @Operation(summary = "创建新预约")
    public ResponseEntity<Object> createReservation(@RequestBody Reservation reservation) {
        // 参数验证
        if (reservation.getStartTime() == null) {
            return ResponseEntity.badRequest().body("开始时间不能为空");
        }
        if (reservation.getEndTime() == null) {
            return ResponseEntity.badRequest().body("结束时间不能为空");
        }
        if (reservation.getVisitorId() == null) {
            return ResponseEntity.badRequest().body("访客ID不能为空");
        }
        
        // 检查日期配额
        try {
            if (!quotaSettingService.isQuotaAvailable(reservation.getStartTime().toLocalDate())) {
                return ResponseEntity.badRequest().body("当日预约人数已满");
            }
            
            // 设置默认值
            reservation.setCreateTime(LocalDateTime.now());
            reservation.setUpdateTime(LocalDateTime.now());
            reservation.setStatus("pending");
            reservation.setHostConfirm("pending");
            
            reservationService.save(reservation);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("创建预约失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "取消预约")
    public ResponseEntity<Boolean> cancelReservation(
            @Parameter(description = "预约ID") 
            @PathVariable Integer id) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(id);
        reservation.setStatus("canceled");
        boolean success = reservationService.updateById(reservation);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改预约信息")
    public ResponseEntity<Boolean> updateReservation(
            @Parameter(description = "预约ID") 
            @PathVariable Integer id,
            @RequestBody Reservation reservation) {
        reservation.setReservationId(id);
        reservation.setUpdateTime(LocalDateTime.now());  // 设置更新时间
        boolean success = reservationService.updateById(reservation);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "被访人确认预约")
    public ResponseEntity<Boolean> confirmReservation(
            @Parameter(description = "预约ID") 
            @PathVariable Integer id) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(id);
        reservation.setHostConfirm("confirmed");
        reservation.setUpdateTime(LocalDateTime.now());  // 设置更新时间
        boolean success = reservationService.updateById(reservation);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "被访人拒绝预约")
    public ResponseEntity<Boolean> rejectReservation(
            @Parameter(description = "预约ID") 
            @PathVariable Integer id) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(id);
        reservation.setHostConfirm("rejected");
        reservation.setUpdateTime(LocalDateTime.now());  // 设置更新时间
        boolean success = reservationService.updateById(reservation);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取预约详情")
    public ResponseEntity<Reservation> getReservation(
            @Parameter(description = "预约ID") 
            @PathVariable Integer id) {
        Reservation reservation = reservationService.getById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/visitor/{visitorId}")
    @Operation(summary = "获取访客的预约列表")
    public ResponseEntity<Page<Reservation>> getVisitorReservations(
            @Parameter(description = "访客ID") 
            @PathVariable Integer visitorId,
            @Parameter(description = "页码") 
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") 
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Reservation> page = reservationService.getVisitorReservations(visitorId, pageNum, pageSize);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询预约列表")
    public ResponseEntity<Page<Reservation>> getReservationPage(
            @Parameter(description = "页码") 
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") 
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "预约状态") 
            @RequestParam(required = false) String status,
            @Parameter(description = "开始时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Page<Reservation> page = reservationService.getReservationPage(pageNum, pageSize, status, startTime, endTime);
        return ResponseEntity.ok(page);
    }
}
