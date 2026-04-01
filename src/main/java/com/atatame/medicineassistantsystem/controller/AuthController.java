package com.atatame.medicineassistantsystem.controller;

import com.atatame.medicineassistantsystem.auth.AuthService;
import com.atatame.medicineassistantsystem.common.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<AuthService.LoginResult> login(@RequestBody LoginRequest req) {
        return Result.ok(authService.login(req.getUsername(), req.getPassword()));
    }

    @PostMapping("/logout")
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

