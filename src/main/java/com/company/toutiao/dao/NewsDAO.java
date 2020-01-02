package com.company.toutiao.dao;

import com.company.toutiao.model.News;
import com.company.toutiao.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface NewsDAO {

    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title, link, image, like_count, comment_count, created_date, user_id ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ")  values (#{title}, #{link}, #{image}, #{likeCount}, #{commentCount}, #{createdDate}, #{userId})"})
    int addNews(News news);


    List<News> selectByUserIdAndOffset(@Param("userId") int userId,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit);

//    @Update({"update ", TABLE_NAME, " set password=#{password} where id = #{id}"})
//    void updatePassword(User user);
//
//    @Delete({"delete from", TABLE_NAME, "where id = #{id}"})
//    void deleteById(int id);
}
