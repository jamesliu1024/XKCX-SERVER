package seig.ljm.xkckserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.common.security.RequireRole;
import seig.ljm.xkckserver.entity.AccessDevice;
import seig.ljm.xkckserver.service.AccessDeviceService;

import java.util.List;

import static seig.ljm.xkckserver.common.constant.EnumConstant.Visitor.Role.*;

/**
 * 门禁设备控制器
 *
 * @author ljm
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/api/accessDevice")
//@RequireRole(value = ADMIN)
@Tag(name = "AccessDevice", description = "门禁设备")
public class AccessDeviceController {

    private AccessDeviceService accessDeviceService;
    @Autowired
    public AccessDeviceController(AccessDeviceService accessDeviceService){
        this.accessDeviceService = accessDeviceService;
    }

    @PostMapping
    @Operation(summary = "添加门禁设备", description = "添加新的门禁设备到系统")
    public ApiResult<AccessDevice> addDevice(@RequestBody AccessDevice device) {
        return ApiResult.success(accessDeviceService.addDevice(device));
    }

    @PutMapping("/{deviceId}/status")
    @Operation(summary = "修改设备状态", description = "修改指定设备的状态")
    public ApiResult<Boolean> updateDeviceStatus(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId,
            @Parameter(description = "设备状态") @RequestParam String status) {
        return ApiResult.success(accessDeviceService.updateDeviceStatus(deviceId, status));
    }

    @PutMapping
    @Operation(summary = "更新门禁设备", description = "更新门禁设备信息")
    public ApiResult<AccessDevice> updateDevice(@RequestBody AccessDevice device) {
        return ApiResult.success(accessDeviceService.updateDevice(device));
    }

    @GetMapping("/{deviceId}")
    @Operation(summary = "获取门禁设备", description = "根据设备ID获取门禁设备信息")
    public ApiResult<AccessDevice> getDevice(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId) {
        return ApiResult.success(accessDeviceService.getById(deviceId));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有门禁设备", description = "获取系统中所有门禁设备的列表")
    public ApiResult<List<AccessDevice>> listDevices() {
        return ApiResult.success(accessDeviceService.list());
    }

    @PutMapping("/{deviceId}/emergency")
    @Operation(summary = "紧急控制", description = "对指定设备进行紧急控制操作")
    public ApiResult<Boolean> emergencyControl(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId,
            @Parameter(description = "控制动作：emergency_open或emergency_close") @RequestParam String action,
            @Parameter(description = "操作原因") @RequestParam String reason) {
        return ApiResult.success(accessDeviceService.emergencyControl(deviceId, action, reason));
    }

    @GetMapping("/{deviceId}/status")
    @Operation(summary = "获取设备状态", description = "获取指定设备的当前状态信息")
    public ApiResult<AccessDevice> getDeviceStatus(
            @Parameter(description = "设备ID") @PathVariable Integer deviceId) {
        return ApiResult.success(accessDeviceService.getDeviceStatus(deviceId));
    }
}
