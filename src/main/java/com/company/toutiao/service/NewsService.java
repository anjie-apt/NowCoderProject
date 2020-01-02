package com.company.toutiao.service;

import com.company.toutiao.dao.NewsDAO;
import com.company.toutiao.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int id, int offset, int limit){
        return newsDAO.selectByUserIdAndOffset(id, offset,limit);
    }
}
