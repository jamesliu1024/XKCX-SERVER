package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import seig.ljm.xkckserver.entity.QuotaSetting;


import java.time.ZonedDateTime;
import java.util.List;

/**
 * 配额设置Mapper接口
 *
 * @author ljm
 * @since 2025-02-18
 */
@Mapper
public interface QuotaSettingMapper extends BaseMapper<QuotaSetting> {

    /**
     * 获取指定日期范围内的配额设置
     */
    @Select("SELECT * FROM quota_setting " +
            "WHERE date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY date ASC")
    List<QuotaSetting> getDateRangeQuotas(@Param("startDate") ZonedDateTime startDate,
                                         @Param("endDate") ZonedDateTime endDate);

    /**
     * 获取指定日期的配额设置
     */
    @Select("SELECT * FROM quota_setting WHERE date = #{date}")
    QuotaSetting getDateQuota(@Param("date") ZonedDateTime date);

    /**
     * 增加当前预约数
     */
    @Update("UPDATE quota_setting SET " +
            "current_count = current_count + 1, " +
            "update_time = NOW() " +
            "WHERE quota_id = #{quotaId} " +
            "AND current_count < max_quota")
    int incrementCurrentCount(@Param("quotaId") Integer quotaId);

    /**
     * 减少当前预约数
     */
    @Update("UPDATE quota_setting SET " +
            "current_count = current_count - 1, " +
            "update_time = NOW() " +
            "WHERE quota_id = #{quotaId} " +
            "AND current_count > 0")
    int decrementCurrentCount(@Param("quotaId") Integer quotaId);

    /**
     * 批量更新配额设置
     */
    @Update("<script>" +
            "UPDATE quota_setting SET " +
            "max_quota = #{maxQuota}, " +
            "is_holiday = #{isHoliday}, " +
            "special_event = #{specialEvent}, " +
            "update_time = NOW() " +
            "WHERE date BETWEEN #{startDate} AND #{endDate}" +
            "</script>")
    int batchUpdateQuota(@Param("startDate") ZonedDateTime startDate,
                        @Param("endDate") ZonedDateTime endDate,
                        @Param("maxQuota") Integer maxQuota,
                        @Param("isHoliday") Boolean isHoliday,
                        @Param("specialEvent") String specialEvent);

    /**
     * 检查日期是否存在配额设置
     */
    @Select("SELECT COUNT(*) FROM quota_setting WHERE date = #{date}")
    int checkDateExists(@Param("date") ZonedDateTime date);

    /**
     * 获取指定日期范围内的节假日配额设置
     */
    @Select("SELECT * FROM quota_setting " +
            "WHERE date BETWEEN #{startDate} AND #{endDate} " +
            "AND is_holiday = true " +
            "ORDER BY date ASC")
    List<QuotaSetting> getHolidayQuotas(@Param("startDate") ZonedDateTime startDate,
                                       @Param("endDate") ZonedDateTime endDate);
}

