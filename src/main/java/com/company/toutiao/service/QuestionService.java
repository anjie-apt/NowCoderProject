package com.company.toutiao.service;

import com.company.toutiao.dao.QuestionDAO;
import com.company.toutiao.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService
{
    @Autowired
    private QuestionDAO questionDAO;


    public int addQuestion(Question question) {
        //敏感词过滤
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }


    public List<Question> getLatestNews(int id, int offset, int limit){
        return questionDAO.selectByUserIdAndOffset(id, offset,limit);
    }
}
