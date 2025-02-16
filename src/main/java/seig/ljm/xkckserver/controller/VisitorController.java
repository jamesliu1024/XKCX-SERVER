package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.entity.Visitor;
import seig.ljm.xkckserver.service.VisitorService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/visitor")
@Tag(name = "Visitor", description = "访客信息")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    @PostMapping("/register")
    @Operation(summary = "访客注册")
    public ResponseEntity<Visitor> registerVisitor(@RequestBody Visitor visitor) {
        visitor.setCreateTime(LocalDateTime.now());
        visitor.setStatus("pending");
        visitorService.save(visitor);
        return ResponseEntity.ok(visitor);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取访客详情")
    public ResponseEntity<Visitor> getVisitor(
            @Parameter(description = "访客ID") 
            @PathVariable Integer id) {
        Visitor visitor = visitorService.getById(id);
        return ResponseEntity.ok(visitor);
    }

    @GetMapping("/openid/{openId}")
    @Operation(summary = "根据微信OpenID查询访客")
    public ResponseEntity<Visitor> getVisitorByOpenId(
            @Parameter(description = "微信OpenID") 
            @PathVariable String openId) {
        Visitor visitor = visitorService.getByOpenId(openId);
        return ResponseEntity.ok(visitor);
    }

    @GetMapping("/idNumber/{idNumber}")
    @Operation(summary = "根据证件号码查询访客")
    public ResponseEntity<Visitor> getVisitorByIdNumber(
            @Parameter(description = "证件号码") 
            @PathVariable String idNumber) {
        Visitor visitor = visitorService.getByIdNumber(idNumber);
        return ResponseEntity.ok(visitor);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新访客信息")
    public ResponseEntity<Boolean> updateVisitor(
            @Parameter(description = "访客ID") 
            @PathVariable Integer id,
            @RequestBody Visitor visitor) {
        visitor.setVisitorId(id);
        boolean success = visitorService.updateById(visitor);
        return ResponseEntity.ok(success);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新访客状态")
    public ResponseEntity<Boolean> updateVisitorStatus(
            @Parameter(description = "访客ID") 
            @PathVariable Integer id,
            @Parameter(description = "新状态") 
            @RequestParam String status) {
        boolean success = visitorService.updateVisitorStatus(id, status);
        return ResponseEntity.ok(success);
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "查询部门访客")
    public ResponseEntity<List<Visitor>> getVisitorsByDepartment(
            @Parameter(description = "部门名称") 
            @PathVariable String department) {
        List<Visitor> visitors = visitorService.getByHostDepartment(department);
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/host/{hostName}")
    @Operation(summary = "查询被访人的访客")
    public ResponseEntity<List<Visitor>> getVisitorsByHost(
            @Parameter(description = "被访人姓名") 
            @PathVariable String hostName) {
        List<Visitor> visitors = visitorService.getByHostName(hostName);
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询访客")
    public ResponseEntity<Page<Visitor>> getVisitorPage(
            @Parameter(description = "页码") 
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") 
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "访客状态") 
            @RequestParam(required = false) String status) {
        Page<Visitor> page = visitorService.getVisitorPage(pageNum, pageSize, status);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取访客统计信息")
    public ResponseEntity<Map<String, Object>> getVisitorStats(
            @Parameter(description = "开始时间") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") 
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Map<String, Object> stats = visitorService.getVisitorStats(startTime, endTime);
        return ResponseEntity.ok(stats);
    }
}
