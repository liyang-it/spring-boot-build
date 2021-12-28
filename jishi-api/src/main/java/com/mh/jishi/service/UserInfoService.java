package com.mh.jishi.service;

import com.mh.jishi.entity.TUser;
import com.mh.jishi.vo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserInfoService {
    @Autowired
    private TUserService userService;


    public UserInfo getInfo(Integer userId) {
        TUser user = userService.getById(userId);
        Assert.state(user != null, "用戶不存在");
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName(user.getNickname());
        userInfo.setAvatarUrl(user.getAvatar());
        return userInfo;
    }
}
