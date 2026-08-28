package com.wudao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wudao.mapper")
public class DanceSchoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanceSchoolApplication.class, args);
    }
}
