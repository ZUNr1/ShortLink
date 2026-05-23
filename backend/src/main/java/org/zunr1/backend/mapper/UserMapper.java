package org.zunr1.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.zunr1.backend.entity.User;
import org.zunr1.backend.vo.PasswordVO;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from user order by id")
    List<User> selectAll();
    @Select("select * from user order by id desc")
    List<User> selectAllDesc();
    @Select("select * from user where id = #{id}")
    User selectById(@Param("id") Long id);
    @Select("select * from user where name = #{name}")
    User selectByName(@Param("name") String name);
    @Select("select * from user where email = #{email}")
    User selectByEmail(@Param("email") String email);
    @Select("select password,salt from user where name = #{name}")
    PasswordVO selectPasswordSalt(@Param("name") String name);
    @Insert("insert into user(name, email, password, salt, status,created_at) " +
            "values (#{name}, #{email}, #{password}, #{salt}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);
    @Update("update user set password = #{password}, salt = #{salt} where id = #{id}")
    int updateUserPasswordSalt(@Param("id") Long id, @Param("password") String password, @Param("salt") String salt);
    @Delete("update user set user.status = 0 where id = #{id}")
    int deleteUser(@Param("id") Long id);
    @Select("select status from user where id = #{id}")
    Integer selectStatusById(@Param("id") Long id);
}
