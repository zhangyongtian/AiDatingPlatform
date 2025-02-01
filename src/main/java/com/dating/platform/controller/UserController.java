package com.dating.platform.controller;

import com.dating.platform.dto.ErrorResponse;
import com.dating.platform.dto.LoginRequest;
import com.dating.platform.entity.User;
import com.dating.platform.service.AIService;
import com.dating.platform.service.OssService;
import com.dating.platform.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final OssService ossService;
    private final AIService aiService;
    
    public UserController(UserService userService, OssService ossService, AIService aiService) {
        this.userService = userService;
        this.ossService = ossService;
        this.aiService = aiService;
    }
    
    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 验证请求参数
        if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("用户名和密码不能为空"));
        }
        
        try {
            User user = userService.login(request.getUsername().trim(), request.getPassword().trim());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 处理评分数据
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("gender", user.getGender());
            userInfo.put("age", user.getAge());
            userInfo.put("city", user.getCity());
            userInfo.put("income", user.getIncome());
            userInfo.put("education", user.getEducation());
            userInfo.put("maritalStatus", user.getMaritalStatus());
            userInfo.put("mbti", user.getMbti());
            userInfo.put("height", user.getHeight());
            userInfo.put("avatarUrl", user.getAvatarUrl());
            userInfo.put("beautyScore", user.getBeautyScore());
            userInfo.put("beautyScoreExplanation", user.getBeautyScoreExplanation());
            
            // 解析JSON字符串为对象
            if (user.getCoreValuesScores() != null) {
                userInfo.put("coreValuesScores", user.getCoreValuesScores());
            }
            if (user.getLifestyleScores() != null) {
                userInfo.put("lifestyleScores", user.getLifestyleScores());
            }
            if (user.getCommunicationScores() != null) {
                userInfo.put("communicationScores", user.getCommunicationScores());
            }
            if (user.getConditionScores() != null) {
                userInfo.put("conditionScores", user.getConditionScores());
            }
            if (user.getBeautyAnalysis() != null) {
                userInfo.put("beautyAnalysis", user.getBeautyAnalysis());
            }
            if (user.getBeautySuggestions() != null) {
                userInfo.put("beautySuggestions", user.getBeautySuggestions());
            }
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("获取用户信息失败：" + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public void updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
    }
    
    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // 上传文件到OSS
            String avatarUrl = ossService.uploadFile(file, id.toString());
            
            // 更新用户头像URL
            User user = userService.getUserById(id);
            user.setAvatarUrl(avatarUrl);
            userService.updateUser(user);
            
            // 使用 HashMap 替代 Map.of
            Map<String, String> response = new HashMap<>();
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("头像上传失败：" + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/beauty-test")
    public ResponseEntity<?> testBeauty(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String avatarUrl = request.get("avatarUrl");
            if (avatarUrl == null || avatarUrl.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("头像URL不能为空"));
            }
            
            // 获取用户信息
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 调用大模型API进行颜值分析
            String aiResponse = aiService.analyzeBeauty(avatarUrl);
            
            // 解析AI返回的JSON数据
            ObjectMapper mapper = new ObjectMapper();
            JsonNode result = mapper.readTree(aiResponse);
            
            // 更新用户信息
            user.setBeautyScore(result.get("beautyScore").asInt());
            user.setBeautyScoreExplanation(aiResponse);
            user.setBeautyAnalysis(result.get("beautyAnalysis").toString());
            user.setBeautySuggestions(result.get("beautySuggestions").toString());
            
            userService.updateUser(user);
            
            // 返回分析结果
            Map<String, Object> response = new HashMap<>();
            response.put("beautyScore", user.getBeautyScore());
            response.put("beautyAnalysis", mapper.readTree(user.getBeautyAnalysis()));
            response.put("beautySuggestions", mapper.readTree(user.getBeautySuggestions()));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("颜值测试失败：" + e.getMessage()));
        }
    }

    private Map<String, Object> createAnalysisItem(String label, int score) {
        Map<String, Object> item = new HashMap<>();
        item.put("label", label);
        item.put("score", Math.min(100, Math.max(50, score)));
        return item;
    }

    private String generateBeautyComment(int score) {
        if (score >= 90) {
            return "您的颜值非常出众！五官精致协调，面部轮廓立体，肤质状态良好，整体气质出众。";
        } else if (score >= 80) {
            return "您的颜值很不错！五官搭配和谐，面部轮廓清晰，肤质状态不错，给人以阳光清新的印象。";
        } else if (score >= 70) {
            return "您的颜值在平均水平之上！五官比例适中，面部特征明显，整体给人以舒适自然的感觉。";
        } else {
            return "您的颜值比较自然！五官搭配协调，面部轮廓自然，整体给人以亲和的感觉。";
        }
    }

    private List<String> generateBeautySuggestions(List<Map<String, Object>> analysis) {
        List<String> suggestions = new ArrayList<>();
        
        for (Map<String, Object> item : analysis) {
            int score = (int) item.get("score");
            String label = (String) item.get("label");
            
            if (score < 85) {
                switch (label) {
                    case "五官协调度":
                        suggestions.add("化妆技巧：学习修容和高光技巧，合理利用阴影来增强五官立体感");
                        suggestions.add("眉形设计：根据脸型选择合适的眉形，可以很好地调和五官比例");
                        break;
                    case "面部轮廓":
                        suggestions.add("发型建议：选择适合脸型的发型，如圆脸适合内扣直发，方脸适合蓬松卷发");
                        suggestions.add("妆容技巧：利用腮红和修容的位置来调整脸型视觉效果");
                        break;
                    case "肤质状态":
                        suggestions.add("护肤步骤：坚持基础护肤，注重清洁、保湿、防晒三步曲");
                        suggestions.add("作息调整：保证充足睡眠，每天7-8小时，避免熬夜");
                        break;
                    case "气质表现":
                        suggestions.add("形体训练：练习正确的站姿和走姿，保持挺胸收腹");
                        suggestions.add("穿搭技巧：选择符合场合的着装，注意色彩搭配和整体协调");
                        break;
                }
            }
        }
        
        // 添加通用建议
        suggestions.add("保持规律的作息时间，让身体充分休息和恢复");
        suggestions.add("保持积极乐观的心态，让美丽由内而外自然散发");
        
        return suggestions;
    }
} 