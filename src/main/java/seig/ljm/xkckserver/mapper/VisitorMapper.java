package seig.ljm.xkckserver.mapper;

import seig.ljm.xkckserver.entity.Visitor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
    
    /**
     * 根据微信OpenID查询访客
     */
    @Select("SELECT * FROM Visitor WHERE wechat_openid = #{openId}")
    Visitor selectByOpenId(@Param("openId") String openId);
    
    /**
     * 根据证件号码查询访客
     */
    @Select("SELECT * FROM Visitor WHERE id_number = #{idNumber}")
    Visitor selectByIdNumber(@Param("idNumber") String idNumber);
    
    /**
     * 查询指定部门的访客列表
     */
    @Select("SELECT * FROM Visitor WHERE host_department = #{department} ORDER BY create_time DESC")
    List<Visitor> selectByHostDepartment(@Param("department") String department);
    
    /**
     * 查询被访人的访客列表
     */
    @Select("SELECT * FROM Visitor WHERE host_name = #{hostName} ORDER BY create_time DESC")
    List<Visitor> selectByHostName(@Param("hostName") String hostName);
    
    /**
     * 统计访客信息
     */
    Map<String, Object> selectVisitorStats(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}

