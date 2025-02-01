package com.dating.platform.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private String gender;
    private Integer age;
    private String city;
    private Integer income;
    private Integer education;
    private Integer maritalStatus;
    private String mbti;
    private Integer[] coreValuesScores;
    private Integer[] lifestyleScores;
    private Integer[] communicationScores;
    private Integer[] realConditionScores;
} 