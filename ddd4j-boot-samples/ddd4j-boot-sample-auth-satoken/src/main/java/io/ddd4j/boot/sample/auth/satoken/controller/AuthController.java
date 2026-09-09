package io.ddd4j.boot.sample.auth.satoken.controller;

import io.ddd4j.core.auth.AuthPrincipal;
import io.ddd4j.core.auth.AuthRequest;
import io.ddd4j.core.util.SubjectKit;
import io.ddd4j.spring.annotation.ApplicationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 鉴权示例控制器：演示 SubjectKit 统一鉴权入口（sa-token 底层）。
 *
 * <p>本控制器的代码与 Spring Security / Shiro 示例完全一致，
 * 证明切换底层鉴权框架时业务代码零改动。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@ApplicationService
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * 登录：SubjectKit.login(AuthRequest)
     */
    @PostMapping("/login")
    public Map<String, Object> login(String userId) {
        AuthPrincipal principal = new AuthPrincipal()
                .setLoginId(userId)
                .setUserId(userId)
                .setRoleCode("user");

        AuthRequest request = AuthRequest.of(userId).setTimeout(7200);
        request.setPrincipal(principal);
        String token = SubjectKit.login(request);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("principal", principal);
        return result;
    }

    /**
     * 登出：SubjectKit.logout()
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        SubjectKit.logout();
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return result;
    }

    /**
     * 当前用户：SubjectKit.getPrincipal()
     */
    @GetMapping("/me")
    public Map<String, Object> me() {
        AuthPrincipal principal = SubjectKit.getPrincipal();
        if (principal == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("authenticated", false);
            return result;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", true);
        result.put("loginId", principal.getLoginId());
        result.put("userId", principal.getUserId());
        result.put("roleCode", principal.getRoleCode());
        return result;
    }

    /**
     * 权限校验：SubjectKit.hasPermission()
     */
    @GetMapping("/check/permission")
    public Map<String, Object> checkPermission(String permission) {
        boolean has = SubjectKit.hasPermission(permission);
        Map<String, Object> result = new HashMap<>();
        result.put("permission", permission);
        result.put("has", has);
        return result;
    }

    /**
     * 角色校验：SubjectKit.hasRole()
     */
    @GetMapping("/check/role")
    public Map<String, Object> checkRole(String role) {
        boolean has = SubjectKit.hasRole(role);
        Map<String, Object> result = new HashMap<>();
        result.put("role", role);
        result.put("has", has);
        return result;
    }

    /**
     * 登录状态：SubjectKit.isLogin()
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("login", SubjectKit.isLogin());
        return result;
    }

}
