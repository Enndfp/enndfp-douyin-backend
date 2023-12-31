package com.enndfp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author Enndfp
 */
@SpringBootApplication
@MapperScan("com.enndfp.mapper")
@EnableMongoRepositories
public class DouyinApi {
    public static void main(String[] args) {
        SpringApplication.run(DouyinApi.class, args);
    }
}