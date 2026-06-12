package com.minor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器
 * 在每个请求中校验JWT Token，并将用户信息设置到Spring Security上下文中
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 执行过滤逻辑
     * 从请求头Authorization中提取Token，校验并设置认证信息
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 获取请求URI
        String requestURI = request.getRequestURI();

        // 跳过登录和注册接口，无需Token认证
        if (requestURI.contains("/api/auth/login") || requestURI.contains("/api/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求头中获取Token
        String token = extractToken(request);

        // 如果Token不为空且有效，则设置认证信息
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            // 从Token中解析用户信息
            Long userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // 构造权限列表，角色前加"ROLE_"前缀以符合Spring Security规范
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

            // 创建认证令牌（未认证状态，因为我们已经通过JWT验证了身份）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, userId,
                            Collections.singletonList(authority));

            // 将认证信息设置到SecurityContext中
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 继续执行过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取JWT Token
     * 去掉"Bearer "前缀
     *
     * @param request HTTP请求
     * @return Token字符串，如果不存在则返回null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // 检查Token是否以"Bearer "开头
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // 去掉"Bearer "前缀，返回纯Token
            return bearerToken.substring(7);
        }

        return null;
    }
}