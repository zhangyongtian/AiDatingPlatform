package com.dating.platform.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.dating.platform.service.OssService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    private final OSS ossClient;
    
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    
    @Value("${aliyun.oss.urlPrefix}")
    private String urlPrefix;
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    public OssServiceImpl(OSS ossClient) {
        this.ossClient = ossClient;
    }
    
    @Override
    public String uploadFile(MultipartFile file, String userId) {
        try {
            // 验证文件
            validateFile(file);
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            String fileName = "avatar/" + userId + "/" + UUID.randomUUID() + extension;
            
            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                fileName,
                new ByteArrayInputStream(file.getBytes())
            );
            
            // 上传文件
            ossClient.putObject(putObjectRequest);
            
            // 返回访问URL
            return urlPrefix + "/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
    
    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过5MB");
        }
        
        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("只支持jpg、jpeg、png、gif格式的图片");
        }
    }
} 