package seig.ljm.xkckserver.mapper;

import seig.ljm.xkckserver.entity.Visitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface VisitorMapper extends BaseMapper<Visitor> {
    
    @Results({
        @Result(id = true, column = "visitor_id", property = "visitorId"),
        @Result(column = "name", property = "name"),
        @Result(column = "phone", property = "phone"),
        @Result(column = "wechat_openid", property = "wechatOpenid"),
        @Result(column = "id_type", property = "idType"),
        @Result(column = "id_number", property = "idNumber"),
        @Result(column = "reason", property = "reason"),
        @Result(column = "host_department", property = "hostDepartment"),
        @Result(column = "host_name", property = "hostName"),
        @Result(column = "status", property = "status"),
        @Result(column = "expire_time", property = "expireTime"),
        @Result(column = "create_time", property = "createTime")
    })
    @Select("SELECT * FROM Visitor WHERE wechat_openid = #{openId}")
    Visitor selectByOpenId(@Param("openId") String openId);
    
    @Select("SELECT * FROM Visitor WHERE id_number = #{idNumber}")
    Visitor selectByIdNumber(@Param("idNumber") String idNumber);
    
    @Select("SELECT * FROM Visitor WHERE host_department = #{department} ORDER BY create_time DESC")
    List<Visitor> selectByHostDepartment(@Param("department") String department);
    
    @Select("SELECT * FROM Visitor WHERE host_name = #{hostName} ORDER BY create_time DESC")
    List<Visitor> selectByHostName(@Param("hostName") String hostName);
    
    @Select("SELECT " +
            "COUNT(*) as total_visitors, " +
            "COUNT(CASE WHEN status = 'pending' THEN 1 END) as pending_count, " +
            "COUNT(CASE WHEN status = 'approved' THEN 1 END) as approved_count, " +
            "COUNT(CASE WHEN status = 'expired' THEN 1 END) as expired_count " +
            "FROM Visitor " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    Map<String, Object> selectVisitorStats(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
}

