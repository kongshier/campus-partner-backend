package com.shier.service;

import com.shier.model.domain.Follow;
import com.shier.model.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Shier
* @description 针对表【follow】的数据库操作Service
* @createDate 2023-06-11 13:02:31
*/
public interface FollowService extends IService<Follow> {

    void followUser(Long followUserId, Long userId);

    List<UserVO> listFans(Long userId);

    List<UserVO> listMyFollow(Long userId);
}
