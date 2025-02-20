package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seig.ljm.xkckserver.common.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.Reservation;
import seig.ljm.xkckserver.mapper.ReservationMapper;
import seig.ljm.xkckserver.service.QuotaSettingService;
import seig.ljm.xkckserver.service.ReservationService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.time.ZonedDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * 预约管理服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final QuotaSettingService quotaSettingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation createReservation(Reservation reservation) {
        // 检查时间是否可用
        String timeCheckResult = checkTimeAvailableWithReason(reservation.getStartTime(), reservation.getEndTime());
        if (timeCheckResult != null) {
            throw new RuntimeException(timeCheckResult);
        }

        // 设置初始状态
        reservation.setStatus("pending");
        reservation.setHostConfirm("pending");
        reservation.setCreateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        reservation.setHidden(false);

        // 保存预约
        save(reservation);
        return reservation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation updateReservation(Reservation reservation) {
        // 检查预约是否存在
        Reservation existingReservation = getById(reservation.getReservationId());
        if (existingReservation == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 如果修改了时间，需要检查时间是否可用
        if (!existingReservation.getStartTime().equals(reservation.getStartTime()) ||
            !existingReservation.getEndTime().equals(reservation.getEndTime())) {
            if (!checkTimeAvailable(reservation.getStartTime(), reservation.getEndTime())) {
                throw new RuntimeException("预约时间不可用");
            }
        }

        // 更新预约
        reservation.setUpdateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        updateById(reservation);
        return getById(reservation.getReservationId());
    }

    @Override
    public IPage<Reservation> getReservationPage(Integer current, Integer size, Integer visitorId,
                                               ZonedDateTime startTime, ZonedDateTime endTime, String status) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getHidden, false);

        // 添加查询条件
        if (visitorId != null) {
            wrapper.eq(Reservation::getVisitorId, visitorId);
        }
        if (startTime != null) {
            wrapper.ge(Reservation::getStartTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(Reservation::getEndTime, endTime);
        }
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }

        // 按创建时间降序排序
        wrapper.orderByDesc(Reservation::getCreateTime);

        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<Reservation> getVisitorReservations(Integer visitorId) {
        return reservationMapper.getVisitorReservations(visitorId);
    }

    @Override
    public List<Reservation> getTimeRangeReservations(ZonedDateTime startTime, ZonedDateTime endTime) {
        return reservationMapper.getTimeRangeReservations(startTime, endTime);
    }

    @Override
    public List<Reservation> getPendingReservations() {
        return reservationMapper.getPendingReservations();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateReservationStatus(Integer reservationId, String status) {
        return reservationMapper.updateReservationStatus(reservationId, status, ZonedDateTime.now(TimeZoneConstant.ZONE_ID)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateHostConfirm(Integer reservationId, String hostConfirm) {
        return reservationMapper.updateHostConfirm(reservationId, hostConfirm, ZonedDateTime.now(TimeZoneConstant.ZONE_ID)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteReservation(Integer reservationId) {
        return reservationMapper.softDeleteReservation(reservationId, ZonedDateTime.now(TimeZoneConstant.ZONE_ID)) > 0;
    }

    @Override
    public List<Reservation> getDepartmentReservations(String department) {
        return reservationMapper.getDepartmentReservations(department);
    }

    @Override
    public List<Reservation> getUpcomingReservations() {
        ZonedDateTime now = ZonedDateTime.now(TimeZoneConstant.ZONE_ID);
        ZonedDateTime hourLater = now.plusHours(1);
        return reservationMapper.getUpcomingReservations(now, hourLater);
    }

    @Override
    public Boolean checkTimeAvailable(ZonedDateTime startTime, ZonedDateTime endTime) {
        return checkTimeAvailableWithReason(startTime, endTime) == null;
    }

    @Override
    public IPage<Reservation> getAdminReservationPage(Integer current, Integer size, Integer visitorId,
                                                    ZonedDateTime startTime, ZonedDateTime endTime, String status) {
        LambdaQueryWrapper<Reservation> wrapper = Wrappers.lambdaQuery(Reservation.class);
        if (visitorId != null) {
            wrapper.eq(Reservation::getVisitorId, visitorId);
        }
        if (startTime != null) {
            wrapper.ge(Reservation::getStartTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(Reservation::getEndTime, endTime);
        }
        if (status != null) {
            wrapper.eq(Reservation::getStatus, status);
        }
        wrapper.orderByDesc(Reservation::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    @Override
    public List<Reservation> getAdminVisitorReservations(Integer visitorId) {
        LambdaQueryWrapper<Reservation> wrapper = Wrappers.lambdaQuery(Reservation.class)
                .eq(Reservation::getVisitorId, visitorId)
                .orderByDesc(Reservation::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Reservation> getAdminTimeRangeReservations(ZonedDateTime startTime, ZonedDateTime endTime) {
        LambdaQueryWrapper<Reservation> wrapper = Wrappers.lambdaQuery(Reservation.class)
                .ge(Reservation::getStartTime, startTime)
                .le(Reservation::getEndTime, endTime)
                .orderByDesc(Reservation::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Reservation> getAdminDepartmentReservations(String department) {
        LambdaQueryWrapper<Reservation> wrapper = Wrappers.lambdaQuery(Reservation.class)
                .eq(Reservation::getHostDepartment, department)
                .orderByDesc(Reservation::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Boolean restoreReservation(Integer reservationId) {
        return update(Wrappers.lambdaUpdate(Reservation.class)
                .eq(Reservation::getReservationId, reservationId)
                .set(Reservation::getHidden, false));
    }

    @Override
    public List<Reservation> listReservations(String status, LocalDate date) {
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getHidden, false);
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Reservation::getStatus, status);
        }
        
        if (date != null) {
            // 将LocalDate转换为当天的开始和结束时间
            ZonedDateTime startOfDay = date.atStartOfDay(TimeZoneConstant.ZONE_ID);
            ZonedDateTime endOfDay = date.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID);
            
            wrapper.ge(Reservation::getStartTime, startOfDay)
                   .lt(Reservation::getStartTime, endOfDay);
        }
        
        wrapper.orderByDesc(Reservation::getCreateTime);
        
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer reservationId, String status) {
        Reservation reservation = getById(reservationId);
        if (reservation != null) {
            reservation.setStatus(status);
            reservation.setUpdateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            updateById(reservation);
            
            log.info("Updated reservation {} status to {}", reservationId, status);
        } else {
            log.warn("Reservation {} not found when updating status", reservationId);
        }
    }

    /**
     * 检查时间是否可用，并返回具体原因
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 如果时间可用返回null，否则返回不可用的原因
     */
    private String checkTimeAvailableWithReason(ZonedDateTime startTime, ZonedDateTime endTime) {
        // 检查时间是否合法
        if (startTime.isAfter(endTime)) {
            return "预约开始时间不能晚于结束时间";
        }

        // 检查是否是过去的时间
        if (startTime.isBefore(ZonedDateTime.now(TimeZoneConstant.ZONE_ID))) {
            return "预约开始时间不能早于当前时间";
        }

        // 检查预约日期是否有配额
        LocalDate reservationDate = startTime.toLocalDate();
        if (!quotaSettingService.checkQuotaAvailable(reservationDate)) {
            return "该日期预约配额已满或未设置配额";
        }

        return null;
    }

    @Override
    public Map<String, Object> getPendingReservationCount(LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Reservation::getStatus, "pending")
                  .eq(Reservation::getHidden, false);
            
            // 如果指定了日期，则只统计该日期的预约
            if (date != null) {
                ZonedDateTime startTime = date.atStartOfDay(TimeZoneConstant.ZONE_ID);
                ZonedDateTime endTime = date.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID);
                wrapper.ge(Reservation::getStartTime, startTime)
                       .lt(Reservation::getStartTime, endTime);
            }
            
            // 获取总数
            long totalCount = count(wrapper);
            
            // 按被访部门分组统计
            Map<String, Long> departmentStats = list(wrapper).stream()
                    .collect(Collectors.groupingBy(
                            Reservation::getHostDepartment,
                            Collectors.counting()
                    ));
            
            result.put("totalCount", totalCount);
            result.put("departmentStats", departmentStats);
            result.put("success", true);
            result.put("message", "获取待审核预约数量成功");
            
            if (date != null) {
                result.put("date", date.toString());
            }
        } catch (Exception e) {
            log.error("获取待审核预约数量失败", e);
            result.put("success", false);
            result.put("message", "获取待审核预约数量失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getReservationStatistics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 转换日期为时间戳
            ZonedDateTime startTime = startDate.atStartOfDay(TimeZoneConstant.ZONE_ID);
            ZonedDateTime endTime = endDate.plusDays(1).atStartOfDay(TimeZoneConstant.ZONE_ID);
            
            // 查询指定时间范围内的所有预约
            LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(Reservation::getStartTime, startTime)
                   .lt(Reservation::getStartTime, endTime)
                   .eq(Reservation::getHidden, false);
            
            List<Reservation> reservations = list(wrapper);
            
            // 1. 总预约数量
            result.put("totalReservations", reservations.size());
            
            // 2. 按状态统计
            Map<String, Long> statusStats = reservations.stream()
                    .collect(Collectors.groupingBy(
                            Reservation::getStatus,
                            Collectors.counting()
                    ));
            result.put("statusStats", statusStats);
            
            // 3. 按被访部门统计
            Map<String, Long> departmentStats = reservations.stream()
                    .collect(Collectors.groupingBy(
                            Reservation::getHostDepartment,
                            Collectors.counting()
                    ));
            result.put("departmentStats", departmentStats);
            
            // 4. 按日期统计
            Map<LocalDate, Long> dailyStats = reservations.stream()
                    .collect(Collectors.groupingBy(
                            reservation -> reservation.getStartTime().toLocalDate(),
                            Collectors.counting()
                    ));
            result.put("dailyStats", dailyStats);
            
            // 5. 按主人确认状态统计
            Map<String, Long> hostConfirmStats = reservations.stream()
                    .collect(Collectors.groupingBy(
                            Reservation::getHostConfirm,
                            Collectors.counting()
                    ));
            result.put("hostConfirmStats", hostConfirmStats);
            
            // 6. 计算平均每日预约数
            double avgDailyReservations = reservations.size() / 
                    (startDate.until(endDate.plusDays(1)).getDays() * 1.0);
            result.put("averageDailyReservations", avgDailyReservations);
            
            // 7. 找出预约最多的部门
            Optional<Map.Entry<String, Long>> topDepartment = departmentStats.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());
            if (topDepartment.isPresent()) {
                Map<String, Object> topDepartmentInfo = new HashMap<>();
                topDepartmentInfo.put("department", topDepartment.get().getKey());
                topDepartmentInfo.put("count", topDepartment.get().getValue());
                result.put("topDepartment", topDepartmentInfo);
            }
            
            // 8. 找出预约最多的日期
            Optional<Map.Entry<LocalDate, Long>> peakDate = dailyStats.entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue());
            if (peakDate.isPresent()) {
                Map<String, Object> peakDateInfo = new HashMap<>();
                peakDateInfo.put("date", peakDate.get().getKey().toString());
                peakDateInfo.put("count", peakDate.get().getValue());
                result.put("peakDate", peakDateInfo);
            }
            
            result.put("success", true);
            result.put("message", "获取预约统计数据成功");
            result.put("startDate", startDate.toString());
            result.put("endDate", endDate.toString());
            
        } catch (Exception e) {
            log.error("获取预约统计数据失败", e);
            result.put("success", false);
            result.put("message", "获取预约统计数据失败：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation updateAdminReservation(Reservation reservation, Integer adminId) {
        // 1. 检查预约是否存在
        Reservation existingReservation = getById(reservation.getReservationId());
        if (existingReservation == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 2. 如果修改了时间，需要检查时间是否可用
        if (!existingReservation.getStartTime().equals(reservation.getStartTime()) ||
            !existingReservation.getEndTime().equals(reservation.getEndTime())) {
            String timeCheckResult = checkTimeAvailableWithReason(reservation.getStartTime(), reservation.getEndTime());
            if (timeCheckResult != null) {
                throw new RuntimeException(timeCheckResult);
            }
        }

        // 3. 保留原有的一些字段值
        reservation.setVisitorId(existingReservation.getVisitorId());
        reservation.setCreateTime(existingReservation.getCreateTime());
        reservation.setHidden(existingReservation.getHidden());
        
        // 4. 更新时间和备注
        reservation.setUpdateTime(ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
        if (reservation.getRemarks() == null || reservation.getRemarks().isEmpty()) {
            reservation.setRemarks("管理员修改");
        } else {
            reservation.setRemarks("管理员修改: " + reservation.getRemarks());
        }

        // 5. 更新预约信息
        if (!updateById(reservation)) {
            throw new RuntimeException("更新预约信息失败");
        }

        // 6. 记录操作日志
        try {
            Map<String, Object> details = new HashMap<>();
            details.put("before", existingReservation);
            details.put("after", reservation);
            details.put("adminId", adminId);
            details.put("operationTime", ZonedDateTime.now(TimeZoneConstant.ZONE_ID));
            
            // 这里假设有一个operationLogService来记录操作日志
            // operationLogService.recordOperation(adminId, "UPDATE_RESERVATION", 
            //     reservation.getReservationId(), details);
            
            log.info("管理员{}修改了预约{}", adminId, reservation.getReservationId());
        } catch (Exception e) {
            log.error("记录预约修改操作日志失败", e);
            // 不影响主流程，只记录错误日志
        }

        return getById(reservation.getReservationId());
    }
}
