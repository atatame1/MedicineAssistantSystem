package com.atatame.medicineassistantsystem.auth;

import com.atatame.medicineassistantsystem.exception.BusinessException;
import com.atatame.medicineassistantsystem.model.entity.User;
import com.atatame.medicineassistantsystem.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserService userService;
    private final AuthTokenStore tokenStore = new AuthTokenStore();

    public LoginResult login(String username, String password) {
        if (username == null || username.isBlank()) throw new BusinessException("用户名必填");
        if (password == null || password.isBlank()) throw new BusinessException("密码必填");

        User u = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username).last("limit 1"));
        if (u == null) throw new BusinessException("账号不存在");
        if (u.getStatus() != null && u.getStatus() != 0) throw new BusinessException("账号不可用");
        if (u.getPassword() == null || !u.getPassword().equals(password)) throw new BusinessException("密码错误");

        String token = tokenStore.issue(u.getId());
        return new LoginResult(token, u.getId(), u.getUsername(), u.getNickname());
    }

    public AuthTokenStore.TokenInfo resolve(String token) {
        return tokenStore.get(token);
    }

    public void logout(String token) {
        tokenStore.revoke(token);
    }

    public record LoginResult(String token, Long userId, String username, String nickname) {}
}

