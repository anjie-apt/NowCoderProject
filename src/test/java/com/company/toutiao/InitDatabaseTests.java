package com.company.toutiao;

import com.company.toutiao.dao.NewsDAO;
import com.company.toutiao.dao.UserDAO;
import com.company.toutiao.model.News;
import com.company.toutiao.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class InitDatabaseTests {
	@Autowired
	private UserDAO userDAO;

	@Autowired
	private NewsDAO newsDAO;
	@Test
	void contextLoads() {
		Random random = new Random();
		for (int i = 0; i < 11; i++){
			//增加User
			User user = new User();
			user.setHeadUrl(String.format("http://nowcoder.com/%d", random.nextInt()));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);

			News news = new News();
			news.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*5*i);
			news.setCreatedDate(date);
			news.setImage(String.format("http://images.nowcoder.com/head"));
			news.setLikeCount(i+1);
			news.setUserId(i+1);
			news.setTitle(String.format("TITLE{%d}", i));
			news.setLink(String.format("http://nowcoder.com/%d.html", i));
			newsDAO.addNews(news);


			user.setPassword("newpassword");
			userDAO.updatePassword(user);
		}

		assertEquals("newpassword", userDAO.selectById(10).getPassword());
		userDAO.deleteById(10);
		assertNull(userDAO.selectById(10));

	}

}
