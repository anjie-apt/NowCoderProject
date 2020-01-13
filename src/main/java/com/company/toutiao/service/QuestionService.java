package com.company.toutiao.service;

import com.company.toutiao.dao.QuestionDAO;
import com.company.toutiao.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;


    public Question selectById(int qid) {
        return questionDAO.selectById(qid);
    }

    public int addQuestion(Question question) {
        //html过滤，最基础的做法使用html标签过滤
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //字典树实现敏感词过滤
        question.setTitle(sensitiveService.filter(question.getTitle()));
        question.setContent(sensitiveService.filter(question.getContent()));

        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }

    
    public List<Question> getLatestNews(int id, int offset, int limit){
        return questionDAO.selectByUserIdAndOffset(id, offset,limit);
    }

//    public Question getQuestionById(int questionId) {
//        return questionDAO.getQuestionById(questionId);
//    }


    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
}
