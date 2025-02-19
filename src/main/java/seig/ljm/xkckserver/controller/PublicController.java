package seig.ljm.xkckserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import seig.ljm.xkckserver.common.api.ApiResult;
import seig.ljm.xkckserver.common.constant.VisitorConstant;
import seig.ljm.xkckserver.common.utils.JwtUtils;
import seig.ljm.xkckserver.entity.Visitor;
import seig.ljm.xkckserver.service.VisitorService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static seig.ljm.xkckserver.common.constant.TimeZoneConstant.ZONE_ID;

@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "公共接口", description = "处理登录注册等公共请求")
public class PublicController {

    private final VisitorService visitorService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResult<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String phone = loginRequest.get("phone");
        String password = loginRequest.get("password");

        if (phone == null || password == null) {
            return ApiResult.fail("手机号和密码不能为空");
        }

        // 查找用户
        Visitor visitor = visitorService.getVisitorByPhone(phone);
        if (visitor == null) {
            // return ApiResult.fail("用户不存在");
            return ApiResult.fail("手机号或密码错误");
        }

        // 验证密码
        String hashedPassword = hashPassword(password);
        if (!hashedPassword.equals(visitor.getPasswordHash())) {
            // return ApiResult.fail("密码错误");
            return ApiResult.fail("手机号或密码错误");
        }

        // 更新最后登录时间
        visitorService.updateLastLoginTime(visitor.getVisitorId(), ZonedDateTime.now(ZONE_ID));

        // 生成token
        String token = jwtUtils.generateToken(visitor.getVisitorId(), visitor.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("visitor", visitor);

        return ApiResult.success(result);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResult<Visitor> register(@RequestBody Visitor visitor) {
        // 检查手机号是否已存在
        if (visitorService.getVisitorByPhone(visitor.getPhone()) != null) {
            return ApiResult.fail("手机号已被注册");
        }

        // 设置默认值
        visitor.setRole("visitor");
        visitor.setAccountStatus("normal");
        visitor.setHidden(false);
        visitor.setCreateTime(ZonedDateTime.now(ZONE_ID));
        visitor.setUpdateTime(ZonedDateTime.now(ZONE_ID));

        // 对密码进行哈希处理
        visitor.setPasswordHash(hashPassword(visitor.getPasswordHash()));

        // 保存用户
        visitorService.save(visitor);

        // 查询用户
        Visitor savedVisitor = visitorService.getByVisitorId(visitor.getVisitorId());
        return ApiResult.success(savedVisitor);
    }

    /**
     * 对密码进行哈希处理
     * @param password 密码
     * @return 哈希后的密码
     */ 
    private String hashPassword(String password) {
        try {
            // salt
            String salt = "xkck-ljm-seig";
            StringBuilder sb = new StringBuilder();
            sb.append(salt);
            sb.append(password);
            sb.append(salt);
            String saltedPassword = sb.toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
} 