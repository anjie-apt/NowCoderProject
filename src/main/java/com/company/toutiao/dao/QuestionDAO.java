package com.company.toutiao.dao;

import com.company.toutiao.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface QuestionDAO {
    String TABLE_NAME = "question";
    String INSERT_FIELDS = " title,  content, user_id, created_date, comment_count ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ")  values (#{title}, #{content}, #{userId}, #{createdDate}, #{commentCount})"})
    int addQuestion(Question question);

    @Select({"select ", SELECT_FIELDS, "from ", TABLE_NAME, " where id=#{qid}"})
    Question selectById(int qid);

    List<Question> selectByUserIdAndOffset(@Param("userId") int userId,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    @Update({"update ", TABLE_NAME, " set comment_count=#{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

//    @Delete({"delete from", TABLE_NAME, "where id = #{id}"})
//    void deleteById(int id);
}
