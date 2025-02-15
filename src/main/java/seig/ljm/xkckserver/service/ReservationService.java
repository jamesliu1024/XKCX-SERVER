package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import seig.ljm.xkckserver.entity.Reservation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;

/**
 * <p>
 * 预约服务接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface ReservationService extends IService<Reservation> {
    
    /**
     * 获取访客的预约列表
     */
    Page<Reservation> getVisitorReservations(Integer visitorId, Integer pageNum, Integer pageSize);

    /**
     * 分页查询预约列表
     */
    Page<Reservation> getReservationPage(Integer pageNum, Integer pageSize, String status, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 创建预约
     */
    boolean createReservation(Reservation reservation);

    /**
     * 取消预约
     */
    boolean cancelReservation(Integer reservationId);

    /**
     * 确认预约
     */
    boolean confirmReservation(Integer reservationId);

    /**
     * 拒绝预约
     */
    boolean rejectReservation(Integer reservationId);
}
