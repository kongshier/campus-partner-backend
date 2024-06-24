package com.shier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shier.mapper.FollowMapper;
import com.shier.model.domain.Follow;
import com.shier.model.domain.User;
import com.shier.model.vo.UserVO;
import com.shier.service.FollowService;
import com.shier.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shier
 * @description 针对表【follow】的数据库操作Service实现
 * @createDate 2023-06-11 13:02:31
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
        implements FollowService {

    @Resource
    @Lazy
    private UserService userService;


    @Override
    public void followUser(Long followUserId, Long userId) {
        LambdaQueryWrapper<Follow> followLambdaQueryWrapper = new LambdaQueryWrapper<>();
        followLambdaQueryWrapper.eq(Follow::getFollowUserId, followUserId).eq(Follow::getUserId, userId);
        long count = this.count(followLambdaQueryWrapper);
        if (count == 0) {
            Follow follow = new Follow();
            follow.setFollowUserId(followUserId);
            follow.setUserId(userId);
            this.save(follow);
        } else {
            this.remove(followLambdaQueryWrapper);
        }
    }

    @Override
    public List<UserVO> listFans(Long userId) {
        LambdaQueryWrapper<Follow> followLambdaQueryWrapper = new LambdaQueryWrapper<>();
        followLambdaQueryWrapper.eq(Follow::getFollowUserId, userId);
        List<Follow> list = this.list(followLambdaQueryWrapper);
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        List<User> userList = list.stream().map((follow -> userService.getById(follow.getUserId())))
                .filter(Objects::nonNull).collect(Collectors.toList());
        return userList.stream().map((item) -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(item, userVO);
            LambdaQueryWrapper<Follow> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Follow::getUserId, userId).eq(Follow::getFollowUserId, item.getId());
            long count = this.count(lambdaQueryWrapper);
            userVO.setIsFollow(count > 0);
            return userVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserVO> listMyFollow(Long userId) {
        LambdaQueryWrapper<Follow> followLambdaQueryWrapper = new LambdaQueryWrapper<>();
        followLambdaQueryWrapper.eq(Follow::getUserId, userId);
        List<Follow> list = this.list(followLambdaQueryWrapper);
        List<User> userList = list.stream().map((follow -> userService.getById(follow.getFollowUserId()))).collect(Collectors.toList());
        return userList.stream().map((user) -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVO.setIsFollow(true);
            return userVO;
        }).collect(Collectors.toList());
    }
}




