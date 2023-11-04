package com.enndfp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Enndfp
 */
@SpringBootApplication
@MapperScan("com.enndfp.mapper")
public class DouyinApi {
    public static void main(String[] args) {
        SpringApplication.run(DouyinApi.class, args);
    }
}