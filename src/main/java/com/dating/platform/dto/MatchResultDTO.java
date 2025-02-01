package com.dating.platform.dto;

import lombok.Data;

@Data
public class MatchResultDTO {
    private Long id;
    private String username;
    private String avatarUrl;
    private Integer age;
    private Integer height;
    private String city;
    private Integer education;
    private String mbti;
    private MatchScores scores;

    @Data
    public static class MatchScores {
        private double total;
        private BasicScores basic;
        private double mbti;
        private double values;
        private double lifestyle;
        private double communication;
        private double conditions;
    }

    @Data
    public static class BasicScores {
        private double age;
        private double height;
        private double location;
        private double education;
        private double income;
    }
} 