package org.zunr1.backend.mapper;


import org.apache.ibatis.annotations.*;
import org.zunr1.backend.entity.Link;

import java.util.List;

@Mapper
public interface LinkMapper {

    @Select("SELECT * FROM link WHERE short_code = #{shortCode}")
    Link selectLinkByShortCode(@Param("shortCode") String shortCode);

    @Select("SELECT * FROM link where user_id = #{userId}")
    List<Link> selectLinksByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM link WHERE long_url = #{longUrl}")
    Link selectLinkByLongUrl(@Param("longUrl") String longUrl);

    @Insert("INSERT INTO link (user_id, name, long_url, short_code, expire_at, click_count, created_at, updated_at) " +
            "VALUES (#{userId}, #{name}, #{longUrl}, #{shortCode}, #{expireAt}, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertLink(Link link);
    @Update("UPDATE link SET click_count = #{clickCount} WHERE id = #{id}")
    int updateClickCount(@Param("clickCount") Long clickCount , @Param("id") Long id);
    // LinkMapper.java 中添加
    @Update("UPDATE link SET short_code = #{shortCode} WHERE id = #{id}")
    int updateShortCode(@Param("id") Long id, @Param("shortCode") String shortCode);
}
