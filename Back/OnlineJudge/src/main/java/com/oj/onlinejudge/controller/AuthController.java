package com.oj.onlinejudge.controller;

import com.oj.onlinejudge.common.api.ApiResponse;
import com.oj.onlinejudge.config.JwtProperties;
import com.oj.onlinejudge.domain.dto.LoginRequest;
import com.oj.onlinejudge.domain.dto.RegisterRequest;
import com.oj.onlinejudge.domain.dto.VerifyEmailRequest;
import com.oj.onlinejudge.domain.entity.LoginLog;
import com.oj.onlinejudge.domain.entity.Student;
import com.oj.onlinejudge.domain.entity.Teacher;
import com.oj.onlinejudge.domain.entity.Admin;
import com.oj.onlinejudge.domain.vo.AuthUserVO;
import com.oj.onlinejudge.security.AuthenticatedUser;
import com.oj.onlinejudge.security.JwtTokenProvider;
import com.oj.onlinejudge.security.PasswordService;
import com.oj.onlinejudge.service.LoginLogService;
import com.oj.onlinejudge.service.MailService;
import com.oj.onlinejudge.service.StudentService;
import com.oj.onlinejudge.service.TeacherService;
import com.oj.onlinejudge.service.AdminService;
import com.oj.onlinejudge.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final MailService mailService;
    private final VerificationCodeService verificationCodeService;

    @Value("${app.auth.require-email-verify:true}")
    private boolean requireEmailVerify;

    private static final long VERIFY_EXPIRE_MINUTES = 10L;

    @Operation(summary = "注册(发送验证码或直接注册)", description = "当开启邮箱验证时发送验证码；测试或关闭时直接注册并返回Token")
    @PostMapping("/register")
    public ApiResponse<AuthUserVO> register(@Valid @RequestBody RegisterRequest request,
                                            HttpServletRequest httpReq) {
        // 基础校验：用户名重复
        if (studentService.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 邮箱占用（仅校验正式用户表，待验证数据由 Redis 暂存，不做全局扫描）
        if (StringUtils.hasText(request.getEmail())) {
            boolean emailUsed = studentService.lambdaQuery().eq(Student::getEmail, request.getEmail()).count() > 0;
            if (emailUsed) throw new IllegalArgumentException("邮箱已被使用");
        }

        if (!requireEmailVerify) {
            // 直接创建用户并返回 token（用于测试环境）
            Student s = new Student();
            s.setUsername(request.getUsername());
            s.setPassword(passwordService.encode(request.getPassword()));
            s.setEmail(request.getEmail());
            s.setName(request.getName());
            s.setScore(0);
            s.setIsVerified(true);
            studentService.save(s);
            if (StringUtils.hasText(s.getEmail())) {
                mailService.sendRegisterSuccess(s.getEmail(), s.getUsername(), "student");
            }
            String token = jwtTokenProvider.generateToken(s.getId(), s.getUsername(), "student");
            String bearer = jwtProperties.getPrefix() + " " + token;
            createLoginLog(s.getId(), s.getUsername(), "student", httpReq, true, null);
            AuthUserVO vo = AuthUserVO.builder()
                    .id(s.getId()).username(s.getUsername()).email(s.getEmail()).avatar(s.getAvatar())
                    .role("student").token(bearer)
                    .details(buildDetails(s))
                    .build();
            return ApiResponse.success("注册成功", vo);
        }
        // 开启邮箱验证：存储到 Redis 并发送验证码
        verificationCodeService.savePending(request.getUsername(), request.getEmail(),
                passwordService.encode(request.getPassword()), request.getName(),
                generateAlphaNumCode(6), VERIFY_EXPIRE_MINUTES);
        if (StringUtils.hasText(request.getEmail())) {
            verificationCodeService.get(request.getUsername()).ifPresent(p ->
                    mailService.sendVerificationCode(p.email, p.username, p.code, VERIFY_EXPIRE_MINUTES));
        }
        createLoginLog(null, request.getUsername(), "student", httpReq, true, "register_pending");
        return ApiResponse.success("验证码已发送，请查收邮件并在有效期内完成验证", null);
    }

    @Operation(summary = "验证邮箱并激活", description = "提交验证码，验证通过后创建账号并返回Token")
    @PostMapping("/verifyEmail")
    public ApiResponse<AuthUserVO> verifyEmail(@Valid @RequestBody VerifyEmailRequest req,
                                               HttpServletRequest httpReq) {
        VerificationCodeService.Pending p = verificationCodeService.get(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("未找到待验证的注册信息"));
        if (System.currentTimeMillis() > p.expireEpochMillis) {
            verificationCodeService.remove(req.getUsername());
            throw new IllegalArgumentException("验证码已过期，请重新注册");
        }
        if (!p.code.equalsIgnoreCase(req.getCode())) {
            verificationCodeService.increaseAttempts(req.getUsername());
            throw new IllegalArgumentException("验证码不正确");
        }
        // 创建正式用户
        Student s = new Student();
        s.setUsername(p.username);
        s.setPassword(p.passwordHash);
        s.setEmail(p.email);
        s.setName(p.name);
        s.setScore(0);
        s.setIsVerified(true);
        studentService.save(s);
        // 清理验证码记录
        verificationCodeService.remove(req.getUsername());
        // 发注册成功邮件
        if (StringUtils.hasText(s.getEmail())) {
            mailService.sendRegisterSuccess(s.getEmail(), s.getUsername(), "student");
        }
        // 返回登录态
        String token = jwtTokenProvider.generateToken(s.getId(), s.getUsername(), "student");
        String bearer = jwtProperties.getPrefix() + " " + token;
        createLoginLog(s.getId(), s.getUsername(), "student", httpReq, true, null);
        AuthUserVO vo = AuthUserVO.builder()
                .id(s.getId()).username(s.getUsername()).email(s.getEmail()).avatar(s.getAvatar())
                .role("student")
                .token(bearer)
                .details(buildDetails(s))
                .build();
        return ApiResponse.success("注册并激活成功", vo);
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
                // 更新教师最近登录时间
                teacher.setLastLoginTime(java.time.LocalDateTime.now());
                teacherService.updateById(teacher);
                String tokenT = jwtTokenProvider.generateToken(teacher.getId(), teacher.getUsername(), "teacher");
                String bearerT = jwtProperties.getPrefix() + " " + tokenT;
                createLoginLog(teacher.getId(), teacher.getUsername(), "teacher", httpReq, true, null);
                if (StringUtils.hasText(teacher.getEmail())) {
                    mailService.sendLoginNotice(teacher.getEmail(), teacher.getUsername(), "teacher", httpReq.getRemoteAddr());
                }
                AuthUserVO voT = AuthUserVO.builder()
                        .id(teacher.getId()).username(teacher.getUsername()).email(teacher.getEmail()).avatar(teacher.getAvatar())
                        .role("teacher").token(bearerT)
                        .details(buildDetails(teacher))
                        .build();
                return ApiResponse.success("登录成功", voT);
            case "admin":
                Admin admin = adminService.lambdaQuery()
                        .eq(Admin::getUsername, loginRequest.getUsername()).one();
                if (admin == null) throw new IllegalArgumentException("账号不存在");
                if (!passwordService.matches(loginRequest.getPassword(), admin.getPassword())) {
                    createLoginLog(admin.getId(), admin.getUsername(), "admin", httpReq, false, "密码错误");
                    throw new IllegalArgumentException("账号或密码错误");
                }
                // 更新管理员最近登录时间及IP
                admin.setLastLoginTime(java.time.LocalDateTime.now());
                admin.setLastLoginIp(httpReq.getRemoteAddr());
                adminService.updateById(admin);
                String tokenA = jwtTokenProvider.generateToken(admin.getId(), admin.getUsername(), "admin");
                String bearerA = jwtProperties.getPrefix() + " " + tokenA;
                createLoginLog(admin.getId(), admin.getUsername(), "admin", httpReq, true, null);
                if (StringUtils.hasText(admin.getEmail())) {
                    mailService.sendLoginNotice(admin.getEmail(), admin.getUsername(), "admin", httpReq.getRemoteAddr());
                }
                AuthUserVO voA = AuthUserVO.builder()
                        .id(admin.getId()).username(admin.getUsername()).email(admin.getEmail()).avatar(admin.getAvatar())
                        .role("admin").token(bearerA)
                        .details(buildDetails(admin))
                        .build();
                return ApiResponse.success("登录成功", voA);
            case "student":
            default:
                Student user = studentService.findByUsername(loginRequest.getUsername())
                        .orElseThrow(() -> new IllegalArgumentException("账号不存在"));
                if (!passwordService.matches(loginRequest.getPassword(), user.getPassword())) {
                    createLoginLog(user.getId(), user.getUsername(), "student", httpReq, false, "密码错误");
                    throw new IllegalArgumentException("账号或密码错误");
                }
                if (user.getIsVerified() != null && !user.getIsVerified()) {
                    throw new IllegalArgumentException("邮箱未验证，无法登录");
                }
                // 更新学生最近登录时间及IP
                user.setLastLoginTime(java.time.LocalDateTime.now());
                user.setLastLoginIp(httpReq.getRemoteAddr());
                studentService.updateById(user);
                String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), "student");
                String bearer = jwtProperties.getPrefix() + " " + token;
                createLoginLog(user.getId(), user.getUsername(), "student", httpReq, true, null);
                if (StringUtils.hasText(user.getEmail())) {
                    mailService.sendLoginNotice(user.getEmail(), user.getUsername(), "student", httpReq.getRemoteAddr());
                }
                AuthUserVO vo = AuthUserVO.builder()
                        .id(user.getId()).username(user.getUsername()).email(user.getEmail()).avatar(user.getAvatar())
                        .role("student").token(bearer)
                        .details(buildDetails(user))
                        .build();
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
        // 邮件提醒
        String role = current.getRole();
        if ("student".equals(role)) {
            Student s = studentService.getById(current.getUserId());
            if (s != null && StringUtils.hasText(s.getEmail())) {
                mailService.sendLogoutNotice(s.getEmail(), s.getUsername(), role);
            }
        } else if ("teacher".equals(role)) {
            Teacher t = teacherService.getById(current.getUserId());
            if (t != null && StringUtils.hasText(t.getEmail())) {
                mailService.sendLogoutNotice(t.getEmail(), t.getUsername(), role);
            }
        } else if ("admin".equals(role)) {
            Admin a = adminService.getById(current.getUserId());
            if (a != null && StringUtils.hasText(a.getEmail())) {
                mailService.sendLogoutNotice(a.getEmail(), a.getUsername(), role);
            }
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
        Long id = current.getUserId();
        Object entity;
        switch (role) {
            case "teacher":
                entity = teacherService.getById(id);
                break;
            case "admin":
                entity = adminService.getById(id);
                break;
            case "student":
            default:
                entity = studentService.getById(id);
                break;
        }
        if (entity == null) {
            return ApiResponse.failure(404, "用户不存在");
        }
        java.util.Map<String, Object> details = buildDetails(entity); // 使用统一方法，自动过滤敏感字段
        String username = (String) details.getOrDefault("username", current.getUsername());
        String email = (String) details.get("email");
        String avatar = (String) details.get("avatar");
        // 组装带前缀 token：优先使用已解析的 raw token
        String raw = current.getToken();
        String bearer = raw != null ? jwtProperties.getPrefix() + " " + raw : null;
        AuthUserVO vo = AuthUserVO.builder()
                .id(id).username(username).email(email).avatar(avatar).role(role)
                .token(bearer)
                .details(details)
                .build();
        return ApiResponse.success(vo);
    }

    private void createLoginLog(Long userId, String username, String role, HttpServletRequest req,
                                boolean success, String failReason) {
        LoginLog log = new LoginLog();
        log.setUserId(userId == null ? 0L : userId);
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

    /**
     * 构建用户详情 Map，过滤敏感字段（如 password）
     */
    private java.util.Map<String, Object> buildDetails(Object entity) {
        java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
        if (entity == null) return map;
        for (java.lang.reflect.Field f : entity.getClass().getDeclaredFields()) {
            String name = f.getName();
            if ("password".equalsIgnoreCase(name)) { // 过滤密码
                continue;
            }
            f.setAccessible(true);
            try {
                map.put(name, f.get(entity));
            } catch (IllegalAccessException ignored) {
            }
        }
        return map;
    }

    private String generateAlphaNumCode(int len) {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZ23456789abcdefghjkmnpqrstuvwxyz"; // 去掉易混淆字符
        java.security.SecureRandom r = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(chars.charAt(r.nextInt(chars.length())));
        return sb.toString();
    }
}
