package seig.ljm.xkckserver.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.ApiResult;
import seig.ljm.xkckserver.constant.TimeZoneConstant;
import seig.ljm.xkckserver.entity.MessageLog;
import seig.ljm.xkckserver.service.MessageLogService;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * MQTT消息日志控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/messageLog")
@Tag(name = "MessageLog", description = "消息日志")
public class MessageLogController {

    private MessageLogService messageLogService;
    @Autowired
    public MessageLogController(MessageLogService messageLogService){
        this.messageLogService = messageLogService;
    }

    @GetMapping("/{messageId}")
    @Operation(summary = "获取消息详情", description = "根据消息ID获取详细信息")
    public ApiResult<MessageLog> getMessage(
            @Parameter(description = "消息ID") @PathVariable Integer messageId) {
        return ApiResult.success(messageLogService.getById(messageId));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询消息", description = "支持多条件分页查询消息日志")
    public ApiResult<IPage<MessageLog>> getMessagePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "设备ID") @RequestParam(required = false) Integer deviceId,
            @Parameter(description = "消息状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(messageLogService.getMessagePage(current, size, deviceId, status, startTime, endTime));
    }

    @GetMapping("/device/{deviceId}")
    @Operation(summary = "获取设备消息", description = "获取指定设备的消息日志")
    public ApiResult<List<MessageLog>> getDeviceMessages(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId,
            @Parameter(description = "开始时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) @DateTimeFormat(pattern = TimeZoneConstant.DATE_TIME_PATTERN) ZonedDateTime endTime) {
        return ApiResult.success(messageLogService.getDeviceMessages(deviceId, startTime, endTime));
    }

    @GetMapping("/latest/{deviceId}")
    @Operation(summary = "获取最新消息", description = "获取指定设备的最新消息")
    public ApiResult<MessageLog> getLatestMessage(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId) {
        return ApiResult.success(messageLogService.getLatestMessage(deviceId));
    }

    @PostMapping("/record")
    @Operation(summary = "记录消息", description = "记录一条MQTT消息")
    public ApiResult<MessageLog> recordMessage(@RequestBody MessageLog messageLog) {
        return ApiResult.success(messageLogService.recordMessage(messageLog));
    }

    @PutMapping("/{messageId}/status")
    @Operation(summary = "更新消息状态", description = "更新指定消息的处理状态")
    public ApiResult<Boolean> updateStatus(
            @Parameter(description = "消息ID") @PathVariable Integer messageId,
            @Parameter(description = "新状态") @RequestParam String status) {
        return ApiResult.success(messageLogService.updateStatus(messageId, status));
    }
}
