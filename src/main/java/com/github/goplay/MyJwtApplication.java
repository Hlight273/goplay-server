package com.github.goplay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.github.goplay.mapper")
public class MyJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyJwtApplication.class, args);
	}

}
