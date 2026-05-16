package org.zunr1.backend.mapper;


import org.apache.ibatis.annotations.*;
import org.zunr1.backend.entity.Link;

import java.util.List;

@Mapper
public interface LinkMapper {

    @Select("SELECT * FROM link WHERE short_code = #{shortCode}")
    Link selectLinkByShortCode(@Param("shortCode") String shortCode);

    @Select("SELECT * FROM link where user_id = #{userId}")
    List<Link> selectLinksByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM link WHERE long_url = #{longUrl}")
    Link selectLinkByLongUrl(@Param("longUrl") String longUrl);

    @Insert("INSERT INTO link (user_id, long_url, short_code, expire_at) VALUES (#{userId},#{longUrl},#{shortCode},#{expireAt})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")
    int insertLink(Link link);
}
