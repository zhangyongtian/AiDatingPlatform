package com.dating.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dating.platform.service.MatchService;
import com.dating.platform.dto.MatchResultDTO;
import com.dating.platform.entity.*;
import com.dating.platform.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MatchServiceImpl implements MatchService {
    
    private static final double REALITY_WEIGHT = 0.25;
    private static final double BEAUTY_WEIGHT = 0.20;
    private static final double VALUES_WEIGHT = 0.20;
    private static final double BASIC_WEIGHT = 0.15;
    private static final double COMMUNICATION_WEIGHT = 0.10;
    private static final double LIFESTYLE_WEIGHT = 0.10;

    @Autowired
    private UserMapper userMapper;

    
    @Override
    public List<MatchResultDTO> findMatches(Long userId) {
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 获取潜在匹配用户
        List<User> potentialMatches = userMapper.findPotentialMatches(user);
        
        // 计算匹配度并转换为DTO
        return potentialMatches.stream()
            .map(match -> {
                MatchResultDTO dto = new MatchResultDTO();
                BeanUtils.copyProperties(match, dto);
                
                // 计算匹配分数
                User.MatchScore matchScore = user.calculateMatchScore(match);
                dto.setScores(convertToScores(matchScore));
                
                return dto;
            })
            .sorted(Comparator.comparing(dto -> dto.getScores().getTotal(), Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }

    private MatchResultDTO.MatchScores convertToScores(User.MatchScore matchScore) {
        MatchResultDTO.MatchScores scores = new MatchResultDTO.MatchScores();
        scores.setTotal(matchScore.getTotalScore());
        scores.setBasic(calculateBasicScores(matchScore));
        scores.setMbti(matchScore.getMbtiScore());
        scores.setValues(matchScore.getValuesScore());
        scores.setLifestyle(matchScore.getLifestyleScore());
        scores.setCommunication(matchScore.getCommunicationScore());
        scores.setConditions(matchScore.getConditionsScore());
        return scores;
    }
    
    private MatchResultDTO.BasicScores calculateBasicScores(User.MatchScore matchScore) {
        MatchResultDTO.BasicScores basicScores = new MatchResultDTO.BasicScores();
        // 这里可以根据需要设置具体的基础分数
        basicScores.setAge(matchScore.getBasicScore());
        basicScores.setHeight(matchScore.getBasicScore());
        basicScores.setLocation(matchScore.getBasicScore());
        basicScores.setEducation(matchScore.getBasicScore());
        basicScores.setIncome(matchScore.getBasicScore());
        return basicScores;
    }
    
    private double calculateRealityScore(Long userId1, Long userId2) {
        // 直接使用字符串分数计算
        return calculateScoreMatch(
            userMapper.selectById(userId1).getConditionScores(),
            userMapper.selectById(userId2).getConditionScores()
        );
    }
    
    private double calculateBeautyScore(User user1, User user2) {
        if (user1.getBeautyScore() == null || user2.getBeautyScore() == null) {
            return 0.0;
        }
        // 颜值分数差异不要太大
        int scoreDiff = Math.abs(user1.getBeautyScore() - user2.getBeautyScore());
        return 1.0 - (scoreDiff / 100.0);
    }
    
    private double calculateValuesScore(String scores1, String scores2) {
        return calculateScoreMatch(scores1, scores2);
    }
    
    private double calculateBasicScore(User user1, User user2) {
        double score = 1.0;
        
        // 年龄差异不要太大
        int ageDiff = Math.abs(user1.getAge() - user2.getAge());
        if (ageDiff > 10) {
            score *= 0.8;
        }
        
        // 收入范围比较
        int income1 = parseIncome(user1.getIncome());
        int income2 = parseIncome(user2.getIncome());
        if ("male".equals(user1.getGender())) {
            if (income1 < income2) score *= 0.9;
        } else {
            if (income1 > income2) score *= 0.9;
        }
        
        // 同城加分
        if (Objects.equals(user1.getCity(), user2.getCity())) {
            score *= 1.2;
        }
        
        return Math.min(score, 1.0);
    }
    
    // 解析收入范围，返回范围的中间值
    private int parseIncome(String income) {
        if (income == null) return 0;
        try {
            String[] parts = income.toLowerCase().replace("k", "").split("-");
            int min = Integer.parseInt(parts[0]);
            int max = parts.length > 1 ? Integer.parseInt(parts[1]) : min;
            return (min + max) / 2;
        } catch (Exception e) {
            return 0;
        }
    }
    
    private double calculateCommunicationScore(String scores1, String scores2) {
        return calculateScoreMatch(scores1, scores2);
    }
    
    private double calculateLifestyleScore(String scores1, String scores2) {
        return calculateScoreMatch(scores1, scores2);
    }
    
    private double calculateCosineSimilarity(List<Integer> vector1, List<Integer> vector2) {
        if (vector1.contains(null) || vector2.contains(null)) {
            return 0.0;
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
            norm1 += vector1.get(i) * vector1.get(i);
            norm2 += vector2.get(i) * vector2.get(i);
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // 计算字符串格式的分数匹配度
    private double calculateScoreMatch(String scores1, String scores2) {
        if (scores1 == null || scores2 == null) return 60.0;
        
        String[] values1 = scores1.split("_");
        String[] values2 = scores2.split("_");
        
        if (values1.length != values2.length) return 60.0;
        
        double totalDiff = 0;
        for (int i = 0; i < values1.length; i++) {
            int score1 = Integer.parseInt(values1[i]);
            int score2 = Integer.parseInt(values2[i]);
            totalDiff += Math.abs(score1 - score2);
        }
        
        double avgDiff = totalDiff / values1.length;
        return 100.0 - avgDiff; // 差异越小，分数越高
    }

    private double calculateConditionsScore(String scores1, String scores2) {
        return calculateScoreMatch(scores1, scores2);
    }
} 