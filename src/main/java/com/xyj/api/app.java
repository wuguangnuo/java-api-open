package com.xyj.api;

import com.xyj.api.config.TokenAuthFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication()
// b@EnableTransactionManagement
@MapperScan(basePackages = "com.xyj.api")
@EnableScheduling
public class app {
    public static void main(String[] args) {
        SpringApplication.run(app.class, args);
    }

    @Bean
    public FilterRegistrationBean testFilterRegistration() {
        List<String> url = new ArrayList<>();
        url.add("/test/*"); // 过滤器
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TokenAuthFilter());
        registration.setUrlPatterns(url);
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("testFilter");
        registration.setOrder(1);
        return registration;
    }
}
