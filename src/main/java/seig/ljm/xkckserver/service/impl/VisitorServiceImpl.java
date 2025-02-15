package seig.ljm.xkckserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import seig.ljm.xkckserver.entity.Visitor;
import seig.ljm.xkckserver.mapper.VisitorMapper;
import seig.ljm.xkckserver.service.VisitorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {

    @Autowired
    private VisitorMapper visitorMapper;

    @Override
    public Visitor getByOpenId(String openId) {
        return visitorMapper.selectByOpenId(openId);
    }

    @Override
    public Page<Visitor> getVisitorPage(Integer pageNum, Integer pageSize, String status) {
        Page<Visitor> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Visitor> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        return visitorMapper.selectPage(page, queryWrapper);
    }

    @Override
    public Visitor getByIdNumber(String idNumber) {
        return visitorMapper.selectByIdNumber(idNumber);
    }

    @Override
    public List<Visitor> getByHostDepartment(String department) {
        return visitorMapper.selectByHostDepartment(department);
    }

    @Override
    public List<Visitor> getByHostName(String hostName) {
        return visitorMapper.selectByHostName(hostName);
    }

    @Override
    public boolean updateVisitorStatus(Integer visitorId, String status) {
        Visitor visitor = new Visitor();
        visitor.setVisitorId(visitorId);
        visitor.setStatus(status);
        return updateById(visitor);
    }

    @Override
    public Map<String, Object> getVisitorStats(LocalDateTime startTime, LocalDateTime endTime) {
        return visitorMapper.selectVisitorStats(startTime, endTime);
    }
}
