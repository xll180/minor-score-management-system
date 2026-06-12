package com.minor.config;

import com.minor.entity.Admin;
import com.minor.mapper.AdminMapper;
import com.minor.mapper.TeacherMapper;
import com.minor.mapper.StudentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 数据初始化器
 * 应用启动时执行，检查并初始化默认数据
 */
@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 应用启动后执行初始化操作
     * 1. 检查管理员是否存在，不存在则创建默认管理员
     * 2. 打印初始化完成日志
     *
     * @param args 命令行参数
     */
    @Override
    public void run(String... args) {
        // 检查管理员是否存在，不存在则创建默认管理员
        Long adminCount = adminMapper.selectCount(null);
        if (adminCount == 0) {
            log.info("管理员表为空，开始创建默认管理员账号...");
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("系统管理员");
            admin.setStatus(1);
            adminMapper.insert(admin);
            log.info("默认管理员账号创建成功: username=admin, password=admin123");
        } else {
            log.info("管理员账号已存在，跳过初始化。当前管理员数量: {}", adminCount);
        }

        log.info("数据初始化完成。");
    }
}