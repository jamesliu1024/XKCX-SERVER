package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.Reservation;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 预约管理Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 获取访客的所有预约记录
     */
    @Select("SELECT * FROM reservation " +
            "WHERE visitor_id = #{visitorId} " +
            "AND hidden = false " +
            "ORDER BY create_time DESC")
    List<Reservation> getVisitorReservations(@Param("visitorId") Integer visitorId);

    /**
     * 获取指定时间范围内的预约记录
     */
    @Select("SELECT * FROM reservation " +
            "WHERE start_time BETWEEN #{startTime} AND #{endTime} " +
            "AND hidden = false " +
            "ORDER BY start_time ASC")
    List<Reservation> getTimeRangeReservations(@Param("startTime") ZonedDateTime startTime,
                                              @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取待确认的预约记录
     */
    @Select("SELECT * FROM reservation " +
            "WHERE host_confirm = 'pending' " +
            "AND hidden = false " +
            "ORDER BY create_time ASC")
    List<Reservation> getPendingReservations();

    /**
     * 更新预约状态
     */
    @Update("UPDATE reservation SET " +
            "status = #{status}, " +
            "update_time = #{updateTime} " +
            "WHERE reservation_id = #{reservationId}")
    int updateReservationStatus(@Param("reservationId") Integer reservationId,
                               @Param("status") String status,
                               @Param("updateTime") ZonedDateTime updateTime);

    /**
     * 更新预约接待人确认状态
     */
    @Update("UPDATE reservation SET " +
            "host_confirm = #{hostConfirm}, " +
            "update_time = #{updateTime} " +
            "WHERE reservation_id = #{reservationId}")
    int updateHostConfirm(@Param("reservationId") Integer reservationId,
                         @Param("hostConfirm") String hostConfirm,
                         @Param("updateTime") ZonedDateTime updateTime);

    /**
     * 软删除预约记录
     */
    @Update("UPDATE reservation SET " +
            "hidden = true, " +
            "update_time = #{updateTime} " +
            "WHERE reservation_id = #{reservationId}")
    int softDeleteReservation(@Param("reservationId") Integer reservationId,
                             @Param("updateTime") ZonedDateTime updateTime);

    /**
     * 获取指定部门的预约记录
     */
    @Select("SELECT * FROM reservation " +
            "WHERE host_department = #{department} " +
            "AND hidden = false " +
            "ORDER BY create_time DESC")
    List<Reservation> getDepartmentReservations(@Param("department") String department);

    /**
     * 获取即将开始的预约
     */
    @Select("SELECT * FROM reservation " +
            "WHERE start_time > #{now} " +
            "AND start_time <= #{hourLater} " +
            "AND status = 'confirmed' " +
            "AND hidden = false " +
            "ORDER BY start_time ASC")
    List<Reservation> getUpcomingReservations(@Param("now") ZonedDateTime now,
                                             @Param("hourLater") ZonedDateTime hourLater);
}

