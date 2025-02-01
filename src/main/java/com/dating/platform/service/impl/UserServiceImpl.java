package com.dating.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dating.platform.entity.User;
import com.dating.platform.mapper.UserMapper;
import com.dating.platform.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername().trim());
        if (baseMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 验证用户名和密码
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
            user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("用户名和密码不能为空");
        }
        
        // 去除首尾空格
        user.setUsername(user.getUsername().trim());
        user.setPassword(user.getPassword().trim());
        
        // 初始化默认值
        user.setBeautyScore(0);
        user.setBeautyScoreExplanation("尚未进行颜值评分");
        user.setBeautyAnalysis("{}");
        user.setBeautySuggestions("{}");
        user.setTotalScores("0");
        
        // 保存用户
        baseMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                   .eq("password", password)
                   .eq("deleted", 0);
        
        // 查询用户
        User user = baseMapper.selectOne(queryWrapper);
        
        // 验证用户
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public void updateUser(User user) {
        baseMapper.updateById(user);
    }
} 