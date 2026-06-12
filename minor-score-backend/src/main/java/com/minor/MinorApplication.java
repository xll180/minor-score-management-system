package com.minor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 辅修专业成绩管理系统 - 启动类
 */
@SpringBootApplication
public class MinorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinorApplication.class, args);
        System.out.println("========================================");
        System.out.println("  辅修专业成绩管理系统启动成功！");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}