package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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

/**
 * 预约管理服务实现类
 *
 * @author ljm
 * @since 2025-02-18
 */
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
}
