package com.company.toutiao.dao;


import com.company.toutiao.model.Comment;
import com.company.toutiao.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
                 ")  values (#{userId}, #{content}, #{createdDate}, #{entityId}, #{entityType}, #{status})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, "from ", TABLE_NAME, " where id=#{qid}"})
    Question selectById(int qid);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, "where entity_id=#{entityID} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId,
                                        @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " where entity_id=#{entityID} and entity_type=#{entityType} "})
    int getCommentCount(@Param("id") int id,
                        @Param("commentCount") int commentCount);

    @Update({"update ", TABLE_NAME, "set status=#{status} where id=#{id}"})
    int updateStatus(@Param("commentId") int commentId,
                     @Param("status") int status);
}
