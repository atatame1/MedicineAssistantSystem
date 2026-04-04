package com.atatame.medicineassistantsystem.auth;

import com.atatame.medicineassistantsystem.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_USER_ID = "AUTH_USER_ID";

    private static final Pattern USER_PATH = Pattern.compile("^/api/user/(\\d+)/.*$");

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (!needsAuth(path, request.getMethod())) {
            return true;
        }

        String token = resolveToken(request);
        AuthTokenStore.TokenInfo info = authService.resolve(token);
        if (info == null) {
            writeUnauthorized(response, "未登录");
            return false;
        }

        Matcher m = USER_PATH.matcher(path);
        if (m.matches()) {
            Long pathUserId = Long.valueOf(m.group(1));
            if (!pathUserId.equals(info.getUserId())) {
                writeUnauthorized(response, "无权限");
                return false;
            }
        }

        request.setAttribute(ATTR_USER_ID, info.getUserId());
        return true;
    }

    private static boolean needsAuth(String path, String method) {
        if (path == null) return false;
        if ("OPTIONS".equalsIgnoreCase(method)) return false;
        if (path.startsWith("/api/ai/")) return true;
        if ("/api/user/list".equals(path)) return true;
        if (path.startsWith("/api/user/")) {
            if (path.contains("/favorites")) return true;
            if (path.contains("/settings")) return true;
            if (path.contains("/profile")) return true;
            if (path.contains("/tasks")) return true;
            if (path.contains("/projects")) return true;
            if (path.contains("/reports")) return true;
        }
        return false;
    }

    private static String resolveToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring("Bearer ".length()).trim();
        }
        String x = request.getHeader("X-Auth-Token");
        if (x != null && !x.isBlank()) return x.trim();
        return null;
    }

    private static void writeUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(401);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String json = "{\"success\":false,\"message\":\"" + escape(msg) + "\",\"data\":null}";
        response.getWriter().write(json);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

