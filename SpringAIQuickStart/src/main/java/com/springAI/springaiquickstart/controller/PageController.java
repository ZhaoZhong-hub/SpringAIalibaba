package com.springAI.springaiquickstart.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面路由控制器。
 * <p>
 * 让登录页与主界面各自拥有独立 URL（而不是单页 JS 切换视图）：
 * <ul>
 *     <li>GET /       —— 按登录状态自动分流</li>
 *     <li>GET /login  —— 登录页（已登录则直接进 /chat）</li>
 *     <li>GET /chat   —— 对话主界面（未登录则踢回 /login）</li>
 * </ul>
 * 页面文件放在 resources/static/ 下，通过 forward 保留浏览器地址栏中的路径。
 */
@Controller
public class PageController {

    /** 首页：已登录进 /chat，未登录进 /login */
    @GetMapping("/")
    public String index(HttpSession session) {
        return isLoggedIn(session) ? "redirect:/chat" : "redirect:/login";
    }

    /** 登录页 */
    @GetMapping("/login")
    public String login(HttpSession session) {
        return isLoggedIn(session) ? "redirect:/chat" : "forward:/login.html";
    }

    /** 对话主界面 */
    @GetMapping("/chat")
    public String chat(HttpSession session) {
        return isLoggedIn(session) ? "forward:/chat.html" : "redirect:/login";
    }

    private boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(AuthController.SESSION_USER_KEY) != null;
    }
}
