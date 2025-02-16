package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import seig.ljm.xkckserver.entity.RFIDCard;

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
public interface RFIDCardMapper extends BaseMapper<RFIDCard> {
    
    /**
     * 根据UID查询RFID卡
     */
    @Select("SELECT * FROM RFIDCard WHERE uid = #{uid}")
    RFIDCard selectByUid(@Param("uid") String uid);
    
    /**
     * 根据预约ID查询RFID卡
     */
    @Select("SELECT * FROM RFIDCard WHERE reservation_id = #{reservationId}")
    RFIDCard selectByReservationId(@Param("reservationId") Integer reservationId);
    
    /**
     * 查询过期未归还的RFID卡
     */
    @Select("SELECT * FROM RFIDCard WHERE status = 'issued' AND expiration_time < #{now}")
    List<RFIDCard> selectExpiredCards(@Param("now") LocalDateTime now);
    
    /**
     * 统计RFID卡使用情况
     */
    @Select("SELECT " +
            "COUNT(*) as total_cards, " +
            "COUNT(CASE WHEN status = 'issued' THEN 1 END) as issued_count, " +
            "COUNT(CASE WHEN status = 'returned' THEN 1 END) as returned_count, " +
            "COUNT(CASE WHEN status = 'lost' THEN 1 END) as lost_count, " +
            "COUNT(CASE WHEN status = 'issued' AND expiration_time < NOW() THEN 1 END) as expired_count " +
            "FROM RFIDCard " +
            "WHERE issue_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> selectCardStats(@Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 根据状态统计RFID卡数量
     */
    @Select("SELECT status, COUNT(*) as count " +
            "FROM RFIDCard " +
            "GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 查询即将过期的RFID卡(7天内)
     */
    @Select("SELECT * FROM RFIDCard " +
            "WHERE status = 'issued' " + 
            "AND expiration_time BETWEEN NOW() " +
            "AND DATE_ADD(NOW(), INTERVAL 7 DAY)")
    List<RFIDCard> selectNearExpirationCards();

    /**
     * 查询可用卡片
     */
    @Select("SELECT * FROM RFIDCard WHERE status = 'available' LIMIT 1")
    RFIDCard selectOneAvailableCard();
}

