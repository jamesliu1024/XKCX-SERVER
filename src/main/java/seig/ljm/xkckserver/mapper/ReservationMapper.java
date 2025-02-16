package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import seig.ljm.xkckserver.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    @Select("SELECT COUNT(*) FROM Reservation WHERE DATE(start_time) = #{date} AND status = 'confirmed'")
    Integer getReservationCountByDate(@Param("date") LocalDate date);
    
    @Select("SELECT * FROM Reservation WHERE visitor_id = #{visitorId} ORDER BY create_time DESC")
    List<Reservation> getVisitorAllReservations(@Param("visitorId") Integer visitorId);
    
    @Select("SELECT DATE(start_time) as date, COUNT(*) as count FROM Reservation " +
            "WHERE start_time BETWEEN #{startDate} AND #{endDate} " +
            "AND status = 'confirmed' " +
            "GROUP BY DATE(start_time)")
    List<Map<String, Object>> getReservationStats(@Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);
    
    @Select("SELECT COUNT(*) FROM Reservation " +
            "WHERE visitor_id = #{visitorId} " +
            "AND start_time <= #{endTime} " +
            "AND end_time >= #{startTime} " +
            "AND status IN ('pending', 'confirmed')")
    Integer checkVisitorTimeConflict(@Param("visitorId") Integer visitorId, 
                                   @Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);

    @Select("SELECT * FROM Reservation " +
            "WHERE DATE(start_time) = #{date} " +
            "ORDER BY start_time ASC")
    List<Reservation> getDailyReservations(@Param("date") LocalDate date);

    @Select("SELECT * FROM Reservation " +
            "WHERE status = #{status} " +
            "AND start_time >= #{startTime} " +
            "ORDER BY start_time ASC")
    List<Reservation> getReservationsByStatus(@Param("status") String status, 
                                            @Param("startTime") LocalDateTime startTime);

    @Update("UPDATE Reservation SET status = #{status}, update_time = NOW() " +
            "WHERE reservation_id = #{reservationId}")
    int updateReservationStatus(@Param("reservationId") Integer reservationId, 
                              @Param("status") String status);

    @Select("SELECT * FROM Reservation " +
            "WHERE visitor_id = #{visitorId} " +
            "AND start_time >= #{startTime} " +
            "ORDER BY start_time ASC")
    List<Reservation> getUpcomingReservations(@Param("visitorId") Integer visitorId, 
                                            @Param("startTime") LocalDateTime startTime);
}

