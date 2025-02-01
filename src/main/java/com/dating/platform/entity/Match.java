package com.dating.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("match")
public class Match {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    private Long matchedUserId;
    private Double matchScore;
    private String matchReason;
    private Integer status; // 0: 待处理, 1: 已接受, 2: 已拒绝
    
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
} 