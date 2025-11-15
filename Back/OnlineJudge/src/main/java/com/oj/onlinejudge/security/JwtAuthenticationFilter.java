package com.oj.onlinejudge.security;

// JWT 认证过滤器：
// - 检查白名单与预检请求
// - 从请求头解析 Token
// - 解析用户并注入到 Request Attribute
// - 非法或过期时返回 401 JSON

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.config.JwtProperties;
import io.jsonwebtoken.JwtException;

import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, JwtProperties jwtProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    /**
     * 白名单与 OPTIONS 预检请求不拦截
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        List<String> whitelist = jwtProperties.getWhitelist();
        if (CollectionUtils.isEmpty(whitelist)) {
            return false;
        }
        return whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    /**
     * 过滤逻辑：解析并验证 Token，失败返回 401
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(jwtProperties.getHeader());
        if (!StringUtils.hasText(authHeader)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = jwtTokenProvider.resolveToken(authHeader);
        if (!StringUtils.hasText(token)) {
            writeUnauthorized(response, "缺少有效的Token");
            return;
        }
        try {
            AuthenticatedUser user = jwtTokenProvider.parseUser(token);
            // token 已在 parseUser 设置，这里确保设置请求属性
            request.setAttribute(AuthenticatedUser.REQUEST_ATTRIBUTE, user);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            writeUnauthorized(response, "Token非法或已过期");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> body = ApiResponse.failure(HttpStatus.UNAUTHORIZED.value(), message);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
