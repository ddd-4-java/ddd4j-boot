package io.ddd4j.boot.sample.auth.shiro.controller;

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
 * 鉴权示例控制器：演示 SubjectKit 统一鉴权入口（Shiro 底层）。
 *
 * <p>本控制器的代码与 sa-token / Spring Security 示例完全一致，
 * 证明切换底层鉴权框架时业务代码零改动。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@ApplicationService
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * 构造鉴权示例控制器。
     *
     */
    public AuthController() {
    }

    /**
     * 登录：SubjectKit.login(AuthRequest)。
     *
     * <p>先构造 AuthPrincipal 主体并绑定到 AuthRequest，
     * 再由统一鉴权入口签发令牌，返回令牌与主体信息。
     *
     * @param userId 登录标识，同时用作主体的登录 ID 与用户 ID
     * @return 包含 token 与 principal 的结果映射
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
     * 登出：SubjectKit.logout()。
     *
     * @return 登出结果映射，success 恒为 true
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        SubjectKit.logout();
        return Map.of("success", true);
    }

    /**
     * 当前用户：SubjectKit.getPrincipal()。
     *
     * <p>未登录时返回 authenticated=false 的结果；已登录时返回登录 ID 与用户 ID。
     *
     * @return 含 authenticated 标识的主体信息映射
     */
    @GetMapping("/me")
    public Map<String, Object> me() {
        AuthPrincipal principal = SubjectKit.getPrincipal();
        if (principal == null) {
            return Map.of("authenticated", false);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", true);
        result.put("loginId", principal.getLoginId());
        result.put("userId", principal.getUserId());
        return result;
    }

    /**
     * 权限校验：SubjectKit.hasPermission()。
     *
     * @param permission 待校验的权限标识
     * @return 含 permission 原值与 has 校验结论的结果映射
     */
    @GetMapping("/check/permission")
    public Map<String, Object> checkPermission(String permission) {
        boolean has = SubjectKit.hasPermission(permission);
        return Map.of("permission", permission, "has", has);
    }

    /**
     * 角色校验：SubjectKit.hasRole()。
     *
     * @param role 待校验的角色编码
     * @return 含 role 原值与 has 校验结论的结果映射
     */
    @GetMapping("/check/role")
    public Map<String, Object> checkRole(String role) {
        boolean has = SubjectKit.hasRole(role);
        return Map.of("role", role, "has", has);
    }

    /**
     * 登录状态：SubjectKit.isLogin()。
     *
     * @return 含 login 标识的状态映射，true 表示当前已登录
     */
    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of("login", SubjectKit.isLogin());
    }

}
