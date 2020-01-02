package com.company.toutiao;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.company.toutiao.dao")
public class ToutiaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToutiaoApplication.class, args);
	}

}
