package com.company.toutiao;

import com.company.toutiao.dao.QuestionDAO;
import com.company.toutiao.dao.UserDAO;
import com.company.toutiao.model.Question;
import com.company.toutiao.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class InitDatabaseTests {
	@Autowired
	private UserDAO userDAO;

	@Autowired
	private QuestionDAO questionDAO;
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

			Question question = new Question();
			question.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*5*i);
			question.setCreatedDate(date);
			question.setUserId(i+1);
			question.setTitle(String.format("TITLE{%d}", i));
			questionDAO.addQuestion(question);


			user.setPassword("newpassword");
			userDAO.updatePassword(user);
		}

		assertEquals("newpassword", userDAO.selectById(10).getPassword());
		userDAO.deleteById(10);
		assertNull(userDAO.selectById(10));

	}

}
