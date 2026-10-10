package com.springAI.springaiquickstart.controller;

import com.springAI.springaiquickstart.config.AppProperties;
import com.springAI.springaiquickstart.dto.LoginRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 简单的登录鉴权接口（基于 HttpSession，适合学习演示）。
 * 账号密码来自配置文件 app.security.*，默认 admin / 123456。
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

    /** Session 中保存登录用户名的 key */
    public static final String SESSION_USER_KEY = "LOGIN_USER";

    @Resource
    private AppProperties appProperties;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        String username = request.username();
        String password = request.password();

        boolean matched = username != null
                && username.equals(appProperties.getUsername())
                && password != null
                && password.equals(appProperties.getPassword());

        if (!matched) {
            log.warn("登录失败，用户名: {}", username);
            return ResponseEntity.status(401).body(Map.of(
                    "success", false,
                    "message", "用户名或密码错误"
            ));
        }

        session.setAttribute(SESSION_USER_KEY, username);
        log.info("用户登录成功: {}", username);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "username", username
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("success", true));
    }

    /** 前端用于判断当前是否已登录 */
    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object user = session.getAttribute(SESSION_USER_KEY);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of(
                    "loggedIn", false
            ));
        }
        return ResponseEntity.ok(Map.of(
                "loggedIn", true,
                "username", user.toString()
        ));
    }
}
