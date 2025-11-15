package com.oj.onlinejudge;

// 应用启动入口：启用 SpringBoot，并加载 JWT/密码相关配置属性

import com.oj.onlinejudge.config.JwtProperties;
import com.oj.onlinejudge.config.PasswordProperties;
import com.oj.onlinejudge.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, PasswordProperties.class, StorageProperties.class})
public class OnlineJudgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineJudgeApplication.class, args);
    }
}
