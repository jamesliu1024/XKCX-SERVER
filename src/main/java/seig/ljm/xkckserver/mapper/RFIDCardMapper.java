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
    @Select("SELECT * FROM RFIDCard WHERE status = 'issued' AND expire_time < #{now}")
    List<RFIDCard> selectExpiredCards(@Param("now") LocalDateTime now);
    
    /**
     * 统计RFID卡使用情况
     */
    Map<String, Object> selectCardStats(@Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);
}

