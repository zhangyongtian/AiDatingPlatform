package com.dating.platform.dto;

import com.dating.platform.entity.User;
import lombok.Data;

@Data
public class MatchResult {
    private User matchedUser;
    private double totalScore;
    private double realityScore;
    private double beautyScore;
    private double valuesScore;
    private double basicScore;
    private double communicationScore;
    private double lifestyleScore;
} 