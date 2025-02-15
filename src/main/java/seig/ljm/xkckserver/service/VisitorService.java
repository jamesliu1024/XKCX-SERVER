package seig.ljm.xkckserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import seig.ljm.xkckserver.entity.Visitor;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
public interface VisitorService extends IService<Visitor> {
    
    /**
     * 根据微信OpenID查询访客
     */
    Visitor getByOpenId(String openId);
    
    /**
     * 分页查询访客列表
     */
    Page<Visitor> getVisitorPage(Integer pageNum, Integer pageSize, String status);
    
    /**
     * 根据证件号码查询访客
     */
    Visitor getByIdNumber(String idNumber);
    
    /**
     * 查询指定部门的访客列表
     */
    List<Visitor> getByHostDepartment(String department);
    
    /**
     * 查询被访人的访客列表
     */
    List<Visitor> getByHostName(String hostName);
    
    /**
     * 更新访客状态
     */
    boolean updateVisitorStatus(Integer visitorId, String status);
    
    /**
     * 获取访客统计信息
     */
    Map<String, Object> getVisitorStats(LocalDateTime startTime, LocalDateTime endTime);
}
