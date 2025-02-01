package com.dating.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.*;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String password;
    private String gender;
    private Integer age;
    private String city;
    @TableField("`income`")
    private String income; // 收入范围，格式：5k以下, 5k-10k, 10k-20k, 20k-50k, 50k以上
    private Integer education;
    private Integer maritalStatus;
    private String mbti;
    private String avatarUrl;
    private Integer beautyScore;
    private String beautyScoreExplanation;
    private Integer height;
    
    private String coreValuesScores;
    private String lifestyleScores;
    private String communicationScores;
    private String conditionScores;
    private String totalScores;
    private String beautyAnalysis;
    private String beautySuggestions;
    private String scoreExplanation;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    // 计算与另一个用户的匹配度
    public MatchScore calculateMatchScore(User other) {
        MatchScore score = new MatchScore();
        
        // 计算基础信息匹配度
        score.setBasicScore(roundToTwoDecimals(calculateBasicScore(other)));
        // 计算MBTI匹配度
        score.setMbtiScore(roundToTwoDecimals(calculateMbtiScore(other)));
        // 计算价值观匹配度
        score.setValuesScore(roundToTwoDecimals(calculateValuesScore(other)));
        // 计算生活方式匹配度
        score.setLifestyleScore(roundToTwoDecimals(calculateLifestyleScore(other)));
        // 计算沟通方式匹配度
        score.setCommunicationScore(roundToTwoDecimals(calculateCommunicationScore(other)));
        // 计算现实条件匹配度
        score.setConditionsScore(roundToTwoDecimals(calculateConditionsScore(other)));
        
        // 计算总分
        double totalScore = (
            score.getBasicScore() * 0.2 +
            score.getMbtiScore() * 0.3 +
            score.getValuesScore() * 0.2 +
            score.getLifestyleScore() * 0.1 +
            score.getCommunicationScore() * 0.1 +
            score.getConditionsScore() * 0.1
        );
        score.setTotalScore(roundToTwoDecimals(totalScore));
        
        return score;
    }
    
    // 添加四舍五入到两位小数的辅助方法
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    
    // 内部类：匹配分数
    @Data
    public static class MatchScore {
        private double totalScore;
        private double basicScore;
        private double mbtiScore;
        private double valuesScore;
        private double lifestyleScore;
        private double communicationScore;
        private double conditionsScore;
    }
    
    // 各维度匹配算法实现...
    private double calculateBasicScore(User other) {
        double score = 100.0;
        
        // 年龄差异
        int ageDiff = Math.abs(this.age - other.age);
        score -= ageDiff * 1; // 每差1岁扣2分
        
        // 身高差异
        int heightDiff = Math.abs(this.height - other.height);
        score -= heightDiff * 0.5; // 每差1cm扣0.5分
        
        // 地域匹配
        if (!Objects.equals(this.city, other.city)) {
            score -= 10; // 异地扣20分
        }
        
        // 收入匹配
        int incomeDiff = Math.abs(parseIncome(this.income) - parseIncome(other.income));
        score -= incomeDiff * 0.5; // 每差1k扣0.5分
        
        // 颜值匹配
        if (this.beautyScore != null && other.beautyScore != null) {
            int beautyDiff = Math.abs(this.beautyScore - other.beautyScore);
            score -= beautyDiff * 3; // 每差1分扣3分
        }
        
        return Math.max(0, score);
    }
    
    private double calculateMbtiScore(User other) {
        if (this.mbti == null || other.mbti == null) return 60.0;
        
        // MBTI匹配规则 - 最佳配对关系
        Map<String, List<String>> idealMatches = new HashMap<>();
        // I型人格匹配
        idealMatches.put("ISTJ", Arrays.asList("ISFJ", "ESFJ", "ENTJ", "INTP", "ISTP"));
        idealMatches.put("ISFJ", Arrays.asList("ISTJ", "INTJ", "ENTP", "ESFP", "ESTP"));
        idealMatches.put("INFJ", Arrays.asList("INTJ", "ENTP", "ESFP", "ENFP", "INFP"));
        idealMatches.put("INTJ", Arrays.asList("INFJ", "ISTJ", "ENTP", "ENFP", "INTP"));
        idealMatches.put("ISTP", Arrays.asList("ESFP", "ENFP", "INTP", "ISTJ", "ISFP"));
        idealMatches.put("ISFP", Arrays.asList("INTJ", "ENTP", "ISTJ", "INFP", "ISFJ"));
        idealMatches.put("INFP", Arrays.asList("INTJ", "ENTP", "ESFP", "ENFP", "INFJ"));
        idealMatches.put("INTP", Arrays.asList("INFJ", "ISTJ", "ENTP", "INTP", "INFP"));
        
        // E型人格匹配
        idealMatches.put("ESTP", Arrays.asList("ESFP", "ENFP", "ISTP", "ESTJ", "INTP"));
        idealMatches.put("ESFP", Arrays.asList("ENTP", "INTP", "ISTJ", "ISFP", "ESTP"));
        idealMatches.put("ENFP", Arrays.asList("INTJ", "ENTP", "ISFJ", "INFP", "ENFJ"));
        idealMatches.put("ENTP", Arrays.asList("INFJ", "INTJ", "ENFP", "INFP", "ENTJ"));
        idealMatches.put("ESTJ", Arrays.asList("INTP", "ISTP", "ESFJ", "ESTJ", "ENTJ"));
        idealMatches.put("ESFJ", Arrays.asList("ISFJ", "ESTJ", "ENFJ", "ESFJ", "ENTJ"));
        idealMatches.put("ENFJ", Arrays.asList("INFJ", "ENFP", "ESFJ", "ENFJ", "INTJ"));
        idealMatches.put("ENTJ", Arrays.asList("INTP", "ISTP", "ENTJ", "ENFJ", "ESTJ"));
        
        // 计算匹配分数
        if (idealMatches.containsKey(this.mbti)) {
            List<String> matches = idealMatches.get(this.mbti);
            if (matches.contains(other.mbti)) {
                // 根据匹配位置给分：第一位100分，第二位95分，以此类推
                int position = matches.indexOf(other.mbti);
                return 100.0 - (position * 5.0);
            }
        }
        
        // 计算相似度
        int similarities = 0;
        for (int i = 0; i < 4; i++) {
            if (this.mbti.charAt(i) == other.mbti.charAt(i)) {
                similarities++;
            }
        }
        
        // 基础分60分，每相同一个维度加10分
        return 60.0 + (similarities * 1.0);
    }
    
    private double calculateValuesScore(User other) {
        return calculateScoreMatch(this.coreValuesScores, other.coreValuesScores);
    }
    
    private double calculateLifestyleScore(User other) {
        return calculateScoreMatch(this.lifestyleScores, other.lifestyleScores);
    }
    
    private double calculateCommunicationScore(User other) {
        return calculateScoreMatch(this.communicationScores, other.communicationScores);
    }
    
    private double calculateConditionsScore(User other) {
        return calculateScoreMatch(this.conditionScores, other.conditionScores);
    }
    
    // 辅助方法：计算字符串格式的分数匹配度
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
        return Math.max(0, 100.0 - avgDiff);
    }
    
    // 辅助方法：解析收入范围
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
} 