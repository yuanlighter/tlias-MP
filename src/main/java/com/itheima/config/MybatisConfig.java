package com.itheima.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: yuan
 * @Date: 2025-08-03 19:47
 * @Description:
 */
@Configuration
public class MybatisConfig {
    /*通过在方法上添加 @Bean 注解，
    可以告诉Spring容器这个方法将会返回一个对象，
    该对象应该被注册为Spring IoC容器中的一个bean。*/
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        //初始化MP插件
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        //初始化分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L);

        //将分页插件加入MP插件里面
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);

        //返回MP插件
        return mybatisPlusInterceptor;
    }
}
