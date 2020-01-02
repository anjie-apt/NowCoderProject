package com.company.toutiao;

import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.company.toutiao.dao")
class ToutiaoApplicationTests {

	@Test
	void contextLoads() {
	}

}
