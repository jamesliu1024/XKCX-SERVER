package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.RfidCardRecord;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * RFID卡片使用记录Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface RfidCardRecordMapper extends BaseMapper<RfidCardRecord> {

    /**
     * 获取卡片的使用记录
     */
    @Select("SELECT * FROM rfid_card_record WHERE card_id = #{cardId} AND hidden = false ORDER BY create_time DESC")
    List<RfidCardRecord> getCardRecords(@Param("cardId") Integer cardId);

    /**
     * 获取预约的卡片记录
     */
    @Select("SELECT * FROM rfid_card_record WHERE reservation_id = #{reservationId} AND hidden = false ORDER BY create_time DESC")
    List<RfidCardRecord> getReservationRecords(@Param("reservationId") Integer reservationId);

    /**
     * 获取管理员的操作记录
     */
    @Select("SELECT * FROM rfid_card_record WHERE admin_id = #{adminId} AND hidden = false ORDER BY create_time DESC")
    List<RfidCardRecord> getAdminRecords(@Param("adminId") Integer adminId);

    /**
     * 获取指定时间范围内的记录
     */
    @Select("SELECT * FROM rfid_card_record WHERE create_time BETWEEN #{startTime} AND #{endTime} AND hidden = false ORDER BY create_time DESC")
    List<RfidCardRecord> getTimeRangeRecords(@Param("startTime") ZonedDateTime startTime, @Param("endTime") ZonedDateTime endTime);

    /**
     * 获取卡片最新的使用记录
     */
    @Select("SELECT * FROM rfid_card_record WHERE card_id = #{cardId} AND hidden = false ORDER BY create_time DESC LIMIT 1")
    RfidCardRecord getLatestCardRecord(@Param("cardId") Integer cardId);

    /**
     * 软删除记录
     */
    @Update("UPDATE rfid_card_record SET hidden = true WHERE record_id = #{recordId}")
    int softDeleteRecord(@Param("recordId") Integer recordId);

    /**
     * 获取即将过期的卡片记录
     */
    @Select("SELECT * FROM rfid_card_record WHERE expiration_time BETWEEN #{now} AND #{dayLater} AND operation_type = 'issue' AND hidden = false")
    List<RfidCardRecord> getExpiringRecords(@Param("now") ZonedDateTime now, @Param("dayLater") ZonedDateTime dayLater);

    /**
     * 更新记录的归还时间
     */
    @Update("UPDATE rfid_card_record SET return_time = #{returnTime} WHERE record_id = #{recordId}")
    int updateReturnTime(@Param("recordId") Integer recordId, @Param("returnTime") ZonedDateTime returnTime);

    /**
     * 批量软删除记录
     */
    @Update("<script>" +
            "UPDATE rfid_card_record SET hidden = true " +
            "WHERE record_id IN " +
            "<foreach collection='recordIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchSoftDelete(@Param("recordIds") List<Integer> recordIds);
}

