package com.dating.platform.service;

import com.dating.platform.dto.MatchResultDTO;
import java.util.List;

public interface MatchService {
    /**
     * 查找匹配的用户
     * @param userId 当前用户ID
     * @return 匹配用户列表及匹配分数
     */
    List<MatchResultDTO> findMatches(Long userId);
} 