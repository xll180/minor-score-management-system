package com.minor.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus配置类
 * 实现自动填充功能，在插入和更新时自动填充createTime和updateTime字段
 */
@Configuration
public class MyBatisPlusConfig implements MetaObjectHandler {

    /**
     * 插入数据时自动填充
     * 自动设置createTime和updateTime为当前时间
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();

        // 如果实体中createTime字段为null，则自动填充当前时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);

        // 如果实体中updateTime字段为null，则自动填充当前时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    /**
     * 更新数据时自动填充
     * 自动设置updateTime为当前时间
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时自动填充updateTime为当前时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}