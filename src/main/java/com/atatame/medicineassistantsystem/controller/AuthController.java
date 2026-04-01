package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.auth.AuthService;
import com.atatame.medicineassistantsystem.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证模块", description = "用户登录登出接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "返回登录结果，包括token和用户信息")
    public Result<AuthService.LoginResult> login(@RequestBody LoginRequest req) {
        return Result.ok(authService.login(req.getUsername(), req.getPassword()));
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "根据token进行登出")
    public Result<Void> logout(@RequestBody LogoutRequest req) {
        authService.logout(req.getToken());
        return Result.ok();
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LogoutRequest {
        private String token;
    }
}

