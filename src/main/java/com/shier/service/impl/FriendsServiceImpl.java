package com.shier.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.shier.common.ErrorCode;
import com.shier.mapper.FriendsMapper;
import com.shier.model.domain.Friends;
import com.shier.model.domain.User;
import com.shier.model.request.FriendAddRequest;
import com.shier.model.vo.FriendsRecordVO;
import com.shier.service.FriendsService;
import com.shier.service.UserService;
import com.shier.exception.BusinessException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.shier.constants.FriendConstant.*;
import static com.shier.constants.RedissonConstant.APPLY_LOCK;

/**
 * @author Shier
 * @description 针对表【friends(好友申请管理表)】的数据库操作Service实现
 * @createDate 2023-06-18 14:10:45
 */
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
        implements FriendsService {

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 添加好友
     *
     * @param loginUser
     * @param friendAddRequest
     * @return
     */
    @Override
    public boolean addFriendRecords(User loginUser, FriendAddRequest friendAddRequest) {
        if (StringUtils.isNotBlank(friendAddRequest.getRemark()) && friendAddRequest.getRemark().length() > 120) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证信息最多120个字符");
        }
        if (ObjectUtils.anyNull(loginUser.getId(), friendAddRequest.getReceiveId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "添加失败");
        }
        // 1.添加的不能是自己
        if (Objects.equals(loginUser.getId(), friendAddRequest.getReceiveId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能添加自己为好友");
        }
        RLock lock = redissonClient.getLock(APPLY_LOCK + loginUser.getId());
        try {
            // 抢到锁并执行
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                // 2.条数大于等于1 就不能再添加
                LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
                friendsLambdaQueryWrapper.eq(Friends::getReceiveId, friendAddRequest.getReceiveId());
                friendsLambdaQueryWrapper.eq(Friends::getFromId, loginUser.getId());
                List<Friends> list = this.list(friendsLambdaQueryWrapper);
                list.forEach(friends -> {
                    if (list.size() > 1 && friends.getStatus() == DEFAULT_STATUS) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复申请");
                    }
                });
                Friends newFriend = new Friends();
                newFriend.setFromId(loginUser.getId());
                newFriend.setReceiveId(friendAddRequest.getReceiveId());
                if (StringUtils.isBlank(friendAddRequest.getRemark())) {
                    newFriend.setRemark("我是" + userService.getById(loginUser.getId()).getUsername());
                } else {
                    newFriend.setRemark(friendAddRequest.getRemark());
                }
                newFriend.setCreateTime(new Date());
                return this.save(newFriend);
            }
        } catch (InterruptedException e) {
            log.error("joinTeam error", e);
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    public List<FriendsRecordVO> obtainFriendApplicationRecords(User loginUser) {
        // 查询出当前用户所有申请、同意记录
        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getReceiveId, loginUser.getId());
        return toFriendsVo(friendsLambdaQueryWrapper);
    }

    private List<FriendsRecordVO> toFriendsVo(LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper) {
        List<Friends> friendsList = this.list(friendsLambdaQueryWrapper);
        Collections.reverse(friendsList);
        return friendsList.stream().map(friend -> {
            FriendsRecordVO friendsRecordVO = new FriendsRecordVO();
            BeanUtils.copyProperties(friend, friendsRecordVO);
            User user = userService.getById(friend.getFromId());
            friendsRecordVO.setApplyUser(userService.getSafetyUser(user));
            return friendsRecordVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FriendsRecordVO> getMyRecords(User loginUser) {
        // 查询出当前用户所有申请、同意记录
        LambdaQueryWrapper<Friends> myApplyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        myApplyLambdaQueryWrapper.eq(Friends::getFromId, loginUser.getId());
        List<Friends> friendsList = this.list(myApplyLambdaQueryWrapper);
        Collections.reverse(friendsList);
        return friendsList.stream().map(friend -> {
            FriendsRecordVO friendsRecordVO = new FriendsRecordVO();
            BeanUtils.copyProperties(friend, friendsRecordVO);
            User user = userService.getById(friend.getReceiveId());
            friendsRecordVO.setApplyUser(userService.getSafetyUser(user));
            return friendsRecordVO;
        }).collect(Collectors.toList());
    }

    @Override
    public int getRecordCount(User loginUser) {
        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getReceiveId, loginUser.getId());
        List<Friends> friendsList = this.list(friendsLambdaQueryWrapper);
        int count = 0;
        for (Friends friend : friendsList) {
            if (friend.getStatus() == DEFAULT_STATUS && friend.getIsRead() == NOT_READ) {
                count++;
            }
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toRead(User loginUser, Set<Long> ids) {
        boolean flag = false;
        for (Long id : ids) {
            Friends friend = this.getById(id);
            if (friend.getStatus() == DEFAULT_STATUS && friend.getIsRead() == NOT_READ) {
                friend.setIsRead(READ);
                flag = this.updateById(friend);
            }
        }
        return flag;
    }

    @Override
    public boolean agreeToApply(User loginUser, Long fromId) {
        // 0. 根据receiveId查询所有接收的申请记录
        LambdaQueryWrapper<Friends> friendsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        friendsLambdaQueryWrapper.eq(Friends::getReceiveId, loginUser.getId());
        friendsLambdaQueryWrapper.eq(Friends::getFromId, fromId);
        List<Friends> recordCount = this.list(friendsLambdaQueryWrapper);
        List<Friends> collect = recordCount.stream().filter(f -> f.getStatus() == DEFAULT_STATUS).collect(Collectors.toList());
        // 条数小于1 就不能再同意
        if (collect.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该申请不存在");
        }
        if (collect.size() > 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "操作有误,请重试");
        }
        AtomicBoolean flag = new AtomicBoolean(false);
        collect.forEach(friend -> {
            if (DateUtil.between(new Date(), friend.getCreateTime(), DateUnit.DAY) >= 3 || friend.getStatus() == EXPIRED_STATUS) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "该申请已过期");
            }
            // 1. 分别查询receiveId和fromId的用户，更改userIds中的数据
            User receiveUser = userService.getById(loginUser.getId());
            User fromUser = userService.getById(fromId);
            Set<Long> receiveUserIds = com.shier.utils.StringUtils.stringJsonListToLongSet(receiveUser.getFriendIds());
            Set<Long> fromUserUserIds = com.shier.utils.StringUtils.stringJsonListToLongSet(fromUser.getFriendIds());

            fromUserUserIds.add(receiveUser.getId());
            receiveUserIds.add(fromUser.getId());

            Gson gson = new Gson();
            String jsonFromUserUserIds = gson.toJson(fromUserUserIds);
            String jsonReceiveUserIds = gson.toJson(receiveUserIds);
            receiveUser.setFriendIds(jsonReceiveUserIds);
            fromUser.setFriendIds(jsonFromUserUserIds);
            // 2. 修改状态由0改为1
            friend.setStatus(AGREE_STATUS);
            flag.set(userService.updateById(fromUser) && userService.updateById(receiveUser) && this.updateById(friend));
        });
        return flag.get();
    }

    @Override
    public boolean canceledApply(Long id, User loginUser) {
        QueryWrapper<Friends> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("from_id", id);
        Friends friend = this.getOne(queryWrapper);
        if (friend.getStatus() != DEFAULT_STATUS) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该申请已通过");
        }
        friend.setStatus(REVOKE_STATUS);
        return this.updateById(friend);
    }
}




