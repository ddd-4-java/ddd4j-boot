package io.ddd4j.boot.sample.layered.interfaces;

import io.ddd4j.boot.sample.layered.domain.model.User;
import io.ddd4j.boot.sample.layered.domain.model.UserQuery;
import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.core.api.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口层。
 *
 * <p>直接调用领域层的充血方法（user.save() / user.rename()），
 * 演示"Controller → 领域模型"的简洁调用链，无需 Service 层中转。
 *
 * @author wandl
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 注册用户（充血 save）。
     */
    @PostMapping
    public ApiRestResponse<User> register(@RequestBody RegisterRequest req) {
        // 充血模型：直接 new + save，无需 Service 层
        User user = new User(req.getPhone(), req.getNickname());
        user.save();
        return ApiRestResponse.success(user);
    }

    /**
     * 修改昵称（充血 rename + update）。
     */
    @PostMapping("/{id}/rename")
    public ApiRestResponse<User> rename(@PathVariable String id, @RequestBody RenameRequest req) {
        // 充血查询：new UserQuery().setId(id).one("用户不存在")
        User user = (User) new UserQuery().setId(id).one("用户不存在");
        user.rename(req.getNickname());
        user.update();
        return ApiRestResponse.success(user);
    }

    /**
     * 禁用用户（充血 disable + update）。
     */
    @PostMapping("/{id}/disable")
    public ApiRestResponse<User> disable(@PathVariable String id) {
        User user = (User) new UserQuery().setId(id).one("用户不存在");
        user.disable();
        user.update();
        return ApiRestResponse.success(user);
    }

    /**
     * 按手机号查询（充血查询 one）。
     */
    @GetMapping("/phone/{phone}")
    public ApiRestResponse<User> getByPhone(@PathVariable String phone) {
        User user = (User) new UserQuery().setPhone(phone).one("用户不存在");
        return ApiRestResponse.success(user);
    }

    /**
     * 分页查询（充血查询 page）。
     */
    @GetMapping
    public ApiRestResponse<Page<User>> page(UserQuery query) {
        Page<User> page = query.page();
        return ApiRestResponse.success(page);
    }

    /**
     * 按手机号检查是否已注册（充血断言 notExist）。
     */
    @GetMapping("/phone/{phone}/check")
    public ApiRestResponse<String> checkPhone(@PathVariable String phone) {
        // notExist: 查到就抛异常（该手机号已注册），查不到才通过
        new UserQuery().setPhone(phone).notExist("该手机号已注册");
        return ApiRestResponse.success("手机号可用");
    }

    // === 请求 DTO ===

    public static class RegisterRequest {
        private String phone;
        private String nickname;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    public static class RenameRequest {
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

}
