package seig.ljm.xkckserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seig.ljm.xkckserver.service.AccessDeviceService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ljm
 * @since 2025-02-14
 */
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
    public String get(@Parameter(description = "设备ID") @PathVariable("id") Integer id) {
        return accessDeviceService.getById(id).toString();
    }

}
