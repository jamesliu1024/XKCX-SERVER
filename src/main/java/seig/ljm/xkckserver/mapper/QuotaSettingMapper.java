package seig.ljm.xkckserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import seig.ljm.xkckserver.entity.QuotaSetting;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 配额设置表 Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@Mapper
public interface QuotaSettingMapper extends BaseMapper<QuotaSetting> {
    
    /**
     * 获取指定日期的配额设置
     */
    @Select("SELECT * FROM QuotaSetting WHERE date = #{date}")
    QuotaSetting selectByDate(@Param("date") LocalDate date);
    
    /**
     * 获取日期范围内的配额设置列表
     */
    @Select("SELECT * FROM QuotaSetting WHERE date BETWEEN #{startDate} AND #{endDate} ORDER BY date")
    List<QuotaSetting> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 获取指定日期范围内的最大配额值
     */
    @Select("SELECT MAX(max_quota) FROM QuotaSetting WHERE date BETWEEN #{startDate} AND #{endDate}")
    Integer selectMaxQuotaInRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    /**
     * 获取指定日期已使用的配额数量
     */
    @Select("SELECT COUNT(*) FROM Reservation WHERE DATE(start_time) = #{date} AND status = 'confirmed'")
    Integer selectUsedQuota(@Param("date") LocalDate date);

    /**
     * 根据日期范围统计配额使用情况
     */
    @Select("SELECT " +
            "date, " +
            "max_quota as total_quota, " +
            "(SELECT COUNT(*) FROM Reservation WHERE DATE(start_time) = qs.date AND status = 'confirmed') as used_quota " +
            "FROM QuotaSetting qs " +
            "WHERE date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY date")
    List<Map<String, Object>> selectQuotaStats(@Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);

    /**
     * 获取未来七天的配额设置
     */
    @Select("SELECT quota_id, date, max_quota " +
            "FROM QuotaSetting " +
            "WHERE date >= #{currentDate} " +
            "AND date <= DATE_ADD(#{currentDate}, INTERVAL 7 DAY) " +
            "ORDER BY date")
    List<QuotaSetting> selectUpcomingQuotas(@Param("currentDate") LocalDate currentDate);
}

