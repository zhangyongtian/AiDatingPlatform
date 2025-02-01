package com.dating.platform.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIService {
    
    @Value("${dashscope.api.key}")
    private String apiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public AIService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public String analyzeBeauty(String imageUrl) {
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        
        // 构建提示词
        String prompt = "作为一个堪称苛刻到近乎残忍的颜值评分专家，请用最严格的标准对照片中的人物进行无情的颜值评分。评分标准如下：\n" +
                       "1. 参考标准（这些明星都是经过精修和美颜的照片才能达到这个分数）：\n" +
                       "   - 男性参考：王一博(92分)、肖战(90分)、王嘉尔(88分)、陈伟霆(85分)\n" +
                       "   - 女性参考：迪丽热巴(92分)、杨幂(90分)、范冰冰(88分)、刘亦菲(85分)\n" +
                       "2. 评分区间说明（素人几乎不可能超过90分）：\n" +
                       "   - 95-100分：完全不存在，除非经过整容和高度美颜\n" +
                       "   - 90-94分：极其罕见，需要天生丽质+后天精心打造（万里挑一）\n" +
                       "   - 85-89分：非常优秀，但仍有明显缺陷（千里挑一）\n" +
                       "   - 80-84分：尚可，但与真正的美貌有显著差距（百里挑一）\n" +
                       "   - 75-79分：普通偏上，存在大量缺陷（普通水平）\n" +
                       "   - 70-74分：普通，五官平平（勉强及格）\n" +
                       "   - 65-69分：普通偏下，问题较多\n" +
                       "   - 60-64分：不够理想，需要重点改进\n" +
                       "   - 60分以下：问题严重（请直接列出所有问题）\n" +
                       "3. 评分维度（必须极度挑剔地指出所有缺陷，即使是最细微的瑕疵）：\n" +
                       "   - 五官特征（-40分）：\n" +
                       "     * 眼睛：大小、对称性、眼距、双眼皮宽窄、眼角角度、眼神清澈度\n" +
                       "     * 鼻子：山根高度、鼻翼宽度、鼻头大小、鼻梁直度、鼻孔外露\n" +
                       "     * 嘴巴：唇形优美度、厚薄、唇线清晰度、人中长度、笑容角度\n" +
                       "     * 眉毛：粗细、长度、弧度、眉峰位置、对称性、根根分明度\n" +
                       "   - 轮廓特征（-20分）：\n" +
                       "     * 脸型：黄金比例、对称性、下颌角度、脸部宽窄\n" +
                       "     * 下颌线：清晰度、流畅度、角度、收尾\n" +
                       "     * 额头：大小、形状、发际线\n" +
                       "     * 脸部比例：三庭五眼标准、侧脸角度\n" +
                       "   - 肤质特征（-20分）：\n" +
                       "     * 皮肤：毛孔粗细、痘印、痘坑、皱纹、细纹\n" +
                       "     * 光泽：暗沉、油腻、干燥、发光度、均匀度\n" +
                       "     * 色斑：雀斑、黑眼圈、色素沉着、红血丝\n" +
                       "     * 皮肤状态：水润度、紧致度、年龄痕迹\n" +
                       "   - 气质特征（-20分）：\n" +
                       "     * 精神面貌：神采、气场、精神状态\n" +
                       "     * 整体形象：干净度、精致度、妆容精细度\n" +
                       "     * 气质类型：高级感、优雅度、气场强度\n\n" +
                       "请直接返回JSON数据，格式如下（请根据照片实际情况给出专业、严格且具体的评价）：\n" +
                       "{\n" +
                       "  \"beautyScore\": 分数,\n" +
                       "  \"beautyAnalysis\": [\n" +
                       "    {\"label\": \"五官特征\", \"score\": 分数, \"details\": \"请从眼睛、鼻子、嘴巴、眉毛等方面极其严格地分析，指出所有缺陷\"},\n" +
                       "    {\"label\": \"轮廓特征\", \"score\": 分数, \"details\": \"请从脸型、下颌线、额头、脸部比例等方面极其严格地分析，指出所有缺陷\"},\n" +
                       "    {\"label\": \"肤质特征\", \"score\": 分数, \"details\": \"请从皮肤状态、光泽、色斑等方面极其严格地分析，指出所有缺陷\"},\n" +
                       "    {\"label\": \"气质特征\", \"score\": 分数, \"details\": \"请从精神面貌、整体形象、气质类型等方面极其严格地分析，指出所有缺陷\"}\n" +
                       "  ],\n" +
                       "  \"beautySuggestions\": [\"请给出5条极其具体的改进建议，直指问题要害，可以包括医美、护肤、形体、妆容等方面\"]\n" +
                       "}\n" +
                       "注意事项：\n" +
                       "1. 评分必须极其严格，打分时往低分打，宁可错杀一千，不可放过一个\n" +
                       "2. 分析必须极其专业和直接，像显微镜一样无情地指出所有缺陷，包括最细微的瑕疵\n" +
                       "3. 建议必须极其具体，直指问题要害，给出精确的整改方案\n" +
                       "4. 绝对不要给任何安慰性评价，要让当事人清醒认识到自己的不足\n" +
                       "5. 对于明显的缺陷，要直接指出其影响程度和严重性\n" +
                       "6. 只返回JSON数据，不要包含任何安慰性文字\n" +
                       "7. 请根据照片实际情况给出评价，不要照抄示例\n" +
                       "8. 评分和分析必须对应评分区间的标准，确保前后一致性";
        
        Map<String, Object> input = new HashMap<>();
        input.put("model", "qwen-vl-plus");
        
        List<Map<String, Object>> messages = new ArrayList<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        
        List<Map<String, Object>> messageContents = new ArrayList<>();
        
        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", prompt);
        messageContents.add(textContent);
        
        // 添加图片内容
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        Map<String, String> imageUrlMap = new HashMap<>();
        imageUrlMap.put("url", imageUrl);
        imageContent.put("image_url", imageUrlMap);
        messageContents.add(imageContent);
        
        message.put("content", messageContents);
        messages.add(message);
        input.put("messages", messages);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(input, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            requestEntity,
            String.class
        );
        
        try {
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            JsonNode choices = responseJson.get("choices");
            if (choices == null || choices.size() == 0) {
                throw new RuntimeException("AI响应中没有choices数据");
            }
            
            String aiContent = choices.get(0).get("message").get("content").asText();
            
            // 提取JSON内容
            Pattern pattern = Pattern.compile("```json\\s*\\n(.*?)\\n```", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(aiContent);
            
            if (matcher.find()) {
                String jsonContent = matcher.group(1);
                // 修复JSON格式
                jsonContent = jsonContent.replaceAll("\\}\\},\\s*\\{", "},{");
                jsonContent = jsonContent.replaceAll("\\}\\]\\s*\\},", "}],");
                
                // 验证JSON格式
                try {
                    objectMapper.readTree(jsonContent);
                    return jsonContent;
                } catch (Exception e) {
                    throw new RuntimeException("提取的JSON格式无效：" + jsonContent);
                }
            } else {
                // 如果没有找到markdown代码块，尝试直接解析整个内容
                try {
                    String fixedContent = aiContent.replaceAll("\\}\\},\\s*\\{", "},{");
                    fixedContent = fixedContent.replaceAll("\\}\\]\\s*\\},", "}],");
                    objectMapper.readTree(fixedContent);
                    return fixedContent;
                } catch (Exception e) {
                    throw new RuntimeException("AI返回的内容不是有效的JSON格式：" + aiContent);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("处理AI响应失败：" + e.getMessage());
        }
    }
} 