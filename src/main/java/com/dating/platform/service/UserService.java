package com.dating.platform.service;

import com.dating.platform.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    User register(User user);
    User login(String username, String password);
    User getUserById(Long id);
    void updateUser(User user);
} 