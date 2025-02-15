package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import seig.ljm.xkckserver.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    /**
     * 获取指定日期的预约数量
     */
    @Select("SELECT COUNT(*) FROM Reservation WHERE DATE(start_time) = #{date} AND status = 'confirmed'")
    Integer getReservationCountByDate(@Param("date") LocalDate date);
    
    /**
     * 获取访客的所有预约
     */
    @Select("SELECT * FROM Reservation WHERE visitor_id = #{visitorId} ORDER BY create_time DESC")
    List<Reservation> getVisitorAllReservations(@Param("visitorId") Integer visitorId);
    
    /**
     * 获取指定日期范围内的预约统计
     */
    @Select("SELECT DATE(start_time) as date, COUNT(*) as count FROM Reservation " +
            "WHERE start_time BETWEEN #{startDate} AND #{endDate} " +
            "AND status = 'confirmed' " +
            "GROUP BY DATE(start_time)")
    List<Object> getReservationStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 检查访客在指定时间段是否已有预约
     */
    @Select("SELECT COUNT(*) FROM Reservation " +
            "WHERE visitor_id = #{visitorId} " +
            "AND start_time <= #{endTime} " +
            "AND end_time >= #{startTime} " +
            "AND status IN ('pending', 'confirmed')")
    Integer checkVisitorTimeConflict(@Param("visitorId") Integer visitorId, 
                                   @Param("startTime") LocalDate startTime, 
                                   @Param("endTime") LocalDate endTime);
}

