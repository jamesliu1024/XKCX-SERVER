package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.entity.OperationLog;
import seig.ljm.xkckserver.service.OperationLogService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/operationLog")
@Tag(name = "OperationLog", description = "操作日志")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @PostMapping("/save")
    @Operation(summary = "记录操作日志")
    public ApiResult<Boolean> saveOperationLog(@RequestBody OperationLog operationLog) {
        operationLog.setOperationTime(LocalDateTime.now());
        return ApiResult.success(operationLogService.save(operationLog));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询操作日志")
    public ApiResult<Page<OperationLog>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer operatorId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        Page<OperationLog> page = new Page<>(current, size);
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        
        if (operatorId != null) {
            queryWrapper.eq("operator_id", operatorId);
        }
        if (operationType != null) {
            queryWrapper.eq("operation_type", operationType);
        }
        if (startTime != null && endTime != null) {
            queryWrapper.between("operation_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("operation_time");
        
        return ApiResult.success(operationLogService.page(page, queryWrapper));
    }

    @GetMapping("/{logId}")
    @Operation(summary = "查询操作日志详情")
    public ApiResult<OperationLog> getById(@PathVariable Integer logId) {
        return ApiResult.success(operationLogService.getById(logId));
    }

    @GetMapping("/operator/{operatorId}")
    @Operation(summary = "查询指定操作员的操作记录")
    public ApiResult<List<OperationLog>> getByOperator(
            @PathVariable Integer operatorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("operator_id", operatorId);
        
        if (startTime != null && endTime != null) {
            queryWrapper.between("operation_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("operation_time");
        return ApiResult.success(operationLogService.list(queryWrapper));
    }

    @GetMapping("/type/{operationType}")
    @Operation(summary = "按操作类型查询日志")
    public ApiResult<List<OperationLog>> getByOperationType(
            @PathVariable String operationType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("operation_type", operationType);
        
        if (startTime != null && endTime != null) {
            queryWrapper.between("operation_time", startTime, endTime);
        }
        
        queryWrapper.orderByDesc("operation_time");
        return ApiResult.success(operationLogService.list(queryWrapper));
    }

    @DeleteMapping("/{logId}")
    @Operation(summary = "删除操作日志")
    public ApiResult<Boolean> delete(@PathVariable Integer logId) {
        return ApiResult.success(operationLogService.removeById(logId));
    }
}
