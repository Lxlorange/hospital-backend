package com.itmk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itmk.netSystem.*.mapper")
public class main {

    public static void main(String[] args) {
        SpringApplication.run(main.class, args);
    }

}
