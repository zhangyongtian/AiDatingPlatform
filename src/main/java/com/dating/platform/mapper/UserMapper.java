package com.dating.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dating.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM user WHERE gender != #{gender} AND deleted = 0")
    List<User> findByGenderNot(String gender);
    
    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0 LIMIT 1")
    User findByUsername(String username);
    
    @Select("SELECT * FROM user WHERE gender != #{gender} " +
            "AND age BETWEEN #{minAge} AND #{maxAge} " +
            "AND city = #{city} " +
            "AND deleted = 0")
    IPage<User> findMatchingUsers(IPage<User> page, 
                                @Param("gender") String gender,
                                @Param("minAge") Integer minAge,
                                @Param("maxAge") Integer maxAge,
                                @Param("city") String city);
    
    @Update("UPDATE user SET beauty_score = #{score} WHERE id = #{userId}")
    int updateBeautyScore(@Param("userId") Long userId, @Param("score") Integer score);
    
    @Select("SELECT * FROM user WHERE " +
            "gender != #{gender} " +
            "AND deleted = 0 " +
            "AND beauty_score BETWEEN #{minScore} AND #{maxScore}")
    List<User> findByBeautyScoreRange(@Param("gender") String gender,
                                    @Param("minScore") Integer minScore,
                                    @Param("maxScore") Integer maxScore);
    
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username} AND deleted = 0")
    int countByUsername(String username);
    
    @Select("SELECT * FROM user WHERE " +
            "gender != #{gender} " +
            "AND city = #{city} " +
            "AND deleted = 0")
    List<User> findByCityAndGenderNot(@Param("city") String city, 
                                     @Param("gender") String gender);
    
    @Select("SELECT DISTINCT city FROM user WHERE deleted = 0")
    List<String> findAllCities();
    
    @Update("UPDATE user SET " +
            "income = #{income}, " +
            "education = #{education}, " +
            "marital_status = #{maritalStatus}, " +
            "mbti = #{mbti} " +
            "WHERE id = #{id}")
    int updateUserInfo(@Param("id") Long id,
                      @Param("income") Integer income,
                      @Param("education") Integer education,
                      @Param("maritalStatus") Integer maritalStatus,
                      @Param("mbti") String mbti);
    
    @Select("SELECT * FROM user WHERE " +
            "gender != #{gender} " +
            "AND income >= #{minIncome} " +
            "AND education >= #{minEducation} " +
            "AND deleted = 0")
    List<User> findByConditions(@Param("gender") String gender,
                               @Param("minIncome") Integer minIncome,
                               @Param("minEducation") Integer minEducation);
    
    @Select("SELECT * FROM user WHERE " +
            "gender != #{user.gender} " +  // 异性
            "AND id != #{user.id} " +      // 非本人
            "AND deleted = 0 " +           // 未删除
            "AND age BETWEEN #{user.age} - 10 AND #{user.age} + 10")  // 年龄差距不超过10岁
    List<User> findPotentialMatches(@Param("user") User user);
} 