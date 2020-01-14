package com.company.toutiao.dao;

import com.company.toutiao.model.Comment;
import com.company.toutiao.model.Feed;
import com.company.toutiao.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface FeedDAO {
    String TABLE_NAME = "feed";
    String INSERT_FIELDS = " user_id, data, created_date, type ";
    String SELECT_FIELDS = " id," + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ")  values (#{userId}, #{data}, #{createdDate}, #{type})"})
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, "from ", TABLE_NAME, "where id=#{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
