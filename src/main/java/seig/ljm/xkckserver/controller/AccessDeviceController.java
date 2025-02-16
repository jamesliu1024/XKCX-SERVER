package seig.ljm.xkckserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seig.ljm.xkckserver.entity.AccessDevice;
import seig.ljm.xkckserver.service.AccessDeviceService;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
@Tag(name = "AccessDevice", description = "设备管理")
@RestController
@RequestMapping("/accessDevice")
public class AccessDeviceController {

    private AccessDeviceService accessDeviceService;
    @Autowired
    public AccessDeviceController(AccessDeviceService accessDeviceService) {
        this.accessDeviceService = accessDeviceService;
    }

    /**
     * 根据id获取设备信息
     * @param id 设备id
     * @return 设备信息
     */
    @Operation(summary = "根据id获取设备信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "设备不存在")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccessDevice> get(@Parameter(description = "设备ID") @PathVariable("id") Integer id) {
        return ResponseEntity.ok(accessDeviceService.getById(id));
    }

    @Operation(summary = "获取所有设备列表")
    @GetMapping("/list")
    public ResponseEntity<List<AccessDevice>> list() {
        return ResponseEntity.ok(accessDeviceService.list());
    }

    @Operation(summary = "添加新设备")
    @PostMapping("/add")
    public ResponseEntity<Boolean> add(@RequestBody AccessDevice device) {
        return ResponseEntity.ok(accessDeviceService.save(device));
    }

    @Operation(summary = "更新设备信息")
    @PutMapping("/update")
    public ResponseEntity<Boolean> update(@RequestBody AccessDevice device) {
        return ResponseEntity.ok(accessDeviceService.updateById(device));
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id) {
        return ResponseEntity.ok(accessDeviceService.removeById(id));
    }

    @Operation(summary = "更新设备状态")
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Boolean> updateStatus(
            @PathVariable Integer id,
            @PathVariable String status) {
        AccessDevice device = new AccessDevice();
        device.setDeviceId(id);
        device.setStatus(status);
        return ResponseEntity.ok(accessDeviceService.updateById(device));
    }

    @Operation(summary = "获取在线设备列表")
    @GetMapping("/online")
    public ResponseEntity<List<AccessDevice>> getOnlineDevices() {
        return ResponseEntity.ok(accessDeviceService.lambdaQuery()
                .eq(AccessDevice::getStatus, "online")
                .list());
    }

}
