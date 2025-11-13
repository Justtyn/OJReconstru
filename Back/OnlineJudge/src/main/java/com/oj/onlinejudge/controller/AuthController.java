package com.oj.onlinejudge.controller;

import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.config.JwtProperties;
import com.oj.onlinejudge.domain.dto.LoginRequest;
import com.oj.onlinejudge.domain.dto.RegisterRequest;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.domain.entity.Admin;
import com.oj.onlinejudge.domain.vo.AuthUserVO;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.security.JwtTokenProvider;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.LoginLogService;
import com.oj.onlinejudge.service.StudentService;
import com.oj.onlinejudge.service.TeacherService;
import com.oj.onlinejudge.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证", description = "注册、登录、注销")
public class AuthController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final AdminService adminService;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final LoginLogService loginLogService;

    @Operation(summary = "注册", description = "创建学生账号并返回Token")
    @PostMapping("/register")
    public ApiResponse<AuthUserVO> register(@Valid @RequestBody RegisterRequest request,
                                            HttpServletRequest httpReq) {
        if (studentService.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        Student s = new Student();
        s.setUsername(request.getUsername());
        s.setPassword(passwordService.encode(request.getPassword()));
        s.setEmail(request.getEmail());
        s.setName(request.getName());
        s.setScore(0);
        if (!studentService.save(s)) {
            return ApiResponse.failure(500, "注册失败");
        }
        String token = jwtTokenProvider.generateToken(s.getId(), s.getUsername(), "student");
        String bearer = jwtProperties.getPrefix() + " " + token;
        createLoginLog(s.getId(), s.getUsername(), "student", httpReq, true, null);
        AuthUserVO vo = AuthUserVO.builder()
                .id(s.getId()).username(s.getUsername()).email(s.getEmail()).avatar(s.getAvatar())
                .role("student")
                .token(bearer).build();
        return ApiResponse.success("注册成功", vo);
    }

    @Operation(summary = "登录", description = "用户名密码登录，返回Token")
    @PostMapping("/login")
    public ApiResponse<AuthUserVO> login(@Valid @RequestBody LoginRequest loginRequest,
                                         HttpServletRequest httpReq) {
        String role = StringUtils.hasText(loginRequest.getRole()) ? loginRequest.getRole().toLowerCase() : "student";
        switch (role) {
            case "teacher":
                Teacher teacher = teacherService.lambdaQuery()
                        .eq(Teacher::getUsername, loginRequest.getUsername()).one();
                if (teacher == null) throw new IllegalArgumentException("账号不存在");
                if (!passwordService.matches(loginRequest.getPassword(), teacher.getPassword())) {
                    createLoginLog(teacher.getId(), teacher.getUsername(), "teacher", httpReq, false, "密码错误");
                    throw new IllegalArgumentException("账号或密码错误");
                }
                String tokenT = jwtTokenProvider.generateToken(teacher.getId(), teacher.getUsername(), "teacher");
                String bearerT = jwtProperties.getPrefix() + " " + tokenT;
                createLoginLog(teacher.getId(), teacher.getUsername(), "teacher", httpReq, true, null);
                AuthUserVO voT = AuthUserVO.builder()
                        .id(teacher.getId()).username(teacher.getUsername()).email(teacher.getEmail()).avatar(teacher.getAvatar())
                        .role("teacher").token(bearerT).build();
                return ApiResponse.success("登录成功", voT);
            case "admin":
                Admin admin = adminService.lambdaQuery()
                        .eq(Admin::getUsername, loginRequest.getUsername()).one();
                if (admin == null) throw new IllegalArgumentException("账号不存在");
                if (!passwordService.matches(loginRequest.getPassword(), admin.getPassword())) {
                    createLoginLog(admin.getId(), admin.getUsername(), "admin", httpReq, false, "密码错误");
                    throw new IllegalArgumentException("账号或密码错误");
                }
                String tokenA = jwtTokenProvider.generateToken(admin.getId(), admin.getUsername(), "admin");
                String bearerA = jwtProperties.getPrefix() + " " + tokenA;
                createLoginLog(admin.getId(), admin.getUsername(), "admin", httpReq, true, null);
                AuthUserVO voA = AuthUserVO.builder()
                        .id(admin.getId()).username(admin.getUsername()).email(admin.getEmail()).avatar(admin.getAvatar())
                        .role("admin").token(bearerA).build();
                return ApiResponse.success("登录成功", voA);
            case "student":
            default:
                Student user = studentService.findByUsername(loginRequest.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException("账号不存在"));
                if (!passwordService.matches(loginRequest.getPassword(), user.getPassword())) {
                    createLoginLog(user.getId(), user.getUsername(), "student", httpReq, false, "密码错误");
                    throw new IllegalArgumentException("账号或密码错误");
                }
                String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), "student");
                String bearer = jwtProperties.getPrefix() + " " + token;
                createLoginLog(user.getId(), user.getUsername(), "student", httpReq, true, null);
                AuthUserVO vo = AuthUserVO.builder()
                        .id(user.getId()).username(user.getUsername()).email(user.getEmail()).avatar(user.getAvatar())
                        .role("student").token(bearer).build();
                return ApiResponse.success("登录成功", vo);
        }
    }

    @Operation(summary = "注销", description = "记录登出时间", security = {@SecurityRequirement(name = "BearerAuth")})
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false)
                                    @Parameter(description = "当前认证用户，从请求属性解析") AuthenticatedUser current) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        LoginLog latest = loginLogService.lambdaQuery()
                .eq(LoginLog::getUserId, current.getUserId())
                .eq(LoginLog::getSuccess, true)
                .isNull(LoginLog::getLogoutTime)
                .orderByDesc(LoginLog::getLoginTime)
                .last("limit 1").one();
        if (latest != null) {
            latest.setLogoutTime(java.time.LocalDateTime.now());
            loginLogService.updateById(latest);
        }
        return ApiResponse.success("注销成功", null);
    }

    @Operation(summary = "当前用户信息", description = "根据 Token 返回当前登录用户信息", security = {@SecurityRequirement(name = "BearerAuth")})
    @GetMapping("/users/me")
    public ApiResponse<AuthUserVO> me(@RequestAttribute(value = AuthenticatedUser.REQUEST_ATTRIBUTE, required = false)
                                      @Parameter(description = "当前认证用户") AuthenticatedUser current) {
        if (current == null) {
            return ApiResponse.failure(401, "未登录或Token失效");
        }
        String role = current.getRole();
        String email = null;
        String avatar = null;
        String username = current.getUsername();
        Long id = current.getUserId();
        switch (role) {
            case "teacher":
                Teacher t = teacherService.getById(id);
                if (t == null) return ApiResponse.failure(404, "用户不存在");
                email = t.getEmail();
                avatar = t.getAvatar();
                username = t.getUsername();
                break;
            case "admin":
                Admin a = adminService.getById(id);
                if (a == null) return ApiResponse.failure(404, "用户不存在");
                email = a.getEmail();
                avatar = a.getAvatar();
                username = a.getUsername();
                break;
            case "student":
            default:
                Student s = studentService.getById(id);
                if (s == null) return ApiResponse.failure(404, "用户不存在");
                email = s.getEmail();
                avatar = s.getAvatar();
                username = s.getUsername();
                break;
        }
        AuthUserVO vo = AuthUserVO.builder()
                .id(id).username(username).email(email).avatar(avatar).role(role)
                .build();
        return ApiResponse.success(vo);
    }

    private void createLoginLog(Long userId, String username, String role, HttpServletRequest req,
                                boolean success, String failReason) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setRole(role);
        log.setLoginTime(java.time.LocalDateTime.now());
        log.setSuccess(success);
        log.setFailReason(failReason);
        String ip = req.getRemoteAddr();
        log.setIpAddress(ip);
        String ua = req.getHeader("User-Agent");
        if (StringUtils.hasText(ua)) {
            log.setUserAgent(ua);
            if (ua.contains("Android")) log.setDevice("Android");
            else if (ua.contains("iPhone")) log.setDevice("iPhone");
            else if (ua.contains("Windows")) log.setDevice("Windows");
            else log.setDevice("Other");
        }
        loginLogService.save(log);
    }
}
