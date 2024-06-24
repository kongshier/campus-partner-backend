package com.shier.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shier.common.ErrorCode;
import com.shier.exception.BusinessException;
import com.shier.mapper.ChatMapper;
import com.shier.model.domain.Chat;
import com.shier.model.domain.Team;
import com.shier.model.domain.User;
import com.shier.model.request.ChatRequest;
import com.shier.model.vo.ChatMessageVO;
import com.shier.model.vo.PrivateChatVO;
import com.shier.model.vo.UserVO;
import com.shier.model.vo.WebSocketVO;
import com.shier.service.ChatService;
import com.shier.service.TeamService;
import com.shier.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.shier.constants.ChatConstant.*;
import static com.shier.constants.UserConstants.ADMIN_ROLE;

/**
 * @author Shier
 * @description 针对表【chat(聊天消息表)】的数据库操作Service实现
 * @createDate 2023-06-17 21:50:15
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
        implements ChatService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Override
    public List<ChatMessageVO> getPrivateChat(ChatRequest chatRequest, int chatType, User loginUser) {
        Long toId = chatRequest.getToId();
        if (toId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<ChatMessageVO> chatRecords = getCache(CACHE_CHAT_PRIVATE, loginUser.getId() + String.valueOf(toId));
        if (chatRecords != null) {
            saveCache(CACHE_CHAT_PRIVATE, loginUser.getId() + String.valueOf(toId), chatRecords);
            return chatRecords;
        }
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper.
                and(privateChat -> privateChat.eq(Chat::getFromId, loginUser.getId()).eq(Chat::getToId, toId)
                        .or().
                        eq(Chat::getToId, loginUser.getId()).eq(Chat::getFromId, toId)
                ).eq(Chat::getChatType, chatType);
        // 两方共有聊天
        List<Chat> list = this.list(chatLambdaQueryWrapper);
        List<ChatMessageVO> chatMessageVOList = list.stream().map(chat -> {
            ChatMessageVO ChatMessageVO = chatResult(loginUser.getId(), toId, chat.getText(), chatType, chat.getCreateTime());
            if (chat.getFromId().equals(loginUser.getId())) {
                ChatMessageVO.setIsMy(true);
            }
            return ChatMessageVO;
        }).collect(Collectors.toList());
        saveCache(CACHE_CHAT_PRIVATE, loginUser.getId() + String.valueOf(toId), chatMessageVOList);
        return chatMessageVOList;
    }

    @Override
    public List<ChatMessageVO> getCache(String redisKey, String id) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        List<ChatMessageVO> chatRecords;
        if (redisKey.equals(CACHE_CHAT_HALL)) {
            chatRecords = (List<ChatMessageVO>) valueOperations.get(redisKey);
        } else {
            chatRecords = (List<ChatMessageVO>) valueOperations.get(redisKey + id);
        }
        return chatRecords;
    }

    @Override
    public void saveCache(String redisKey, String id, List<ChatMessageVO> chatMessageVOS) {
        try {
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            // 解决缓存雪崩
            int i = RandomUtil.randomInt(2, 3);
            if (redisKey.equals(CACHE_CHAT_HALL)) {
                valueOperations.set(redisKey, chatMessageVOS, 2 + i / 10, TimeUnit.MINUTES);
            } else {
                valueOperations.set(redisKey + id, chatMessageVOS, 2 + i / 10, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            log.error("redis set key error");
        }
    }

    private ChatMessageVO chatResult(Long userId, String text) {
        ChatMessageVO ChatMessageVO = new ChatMessageVO();
        User fromUser = userService.getById(userId);
        WebSocketVO fromWebSocketVo = new WebSocketVO();
        BeanUtils.copyProperties(fromUser, fromWebSocketVo);
        ChatMessageVO.setFormUser(fromWebSocketVo);
        ChatMessageVO.setText(text);
        return ChatMessageVO;
    }

    @Override
    public ChatMessageVO chatResult(Long userId, Long toId, String text, Integer chatType, Date createTime) {
        ChatMessageVO ChatMessageVO = new ChatMessageVO();
        User fromUser = userService.getById(userId);
        User toUser = userService.getById(toId);
        WebSocketVO fromWebSocketVo = new WebSocketVO();
        WebSocketVO toWebSocketVo = new WebSocketVO();
        BeanUtils.copyProperties(fromUser, fromWebSocketVo);
        BeanUtils.copyProperties(toUser, toWebSocketVo);
        ChatMessageVO.setFormUser(fromWebSocketVo);
        ChatMessageVO.setToUser(toWebSocketVo);
        ChatMessageVO.setChatType(chatType);
        ChatMessageVO.setText(text);
        ChatMessageVO.setCreateTime(DateUtil.format(createTime, "yyyy-MM-dd HH:mm:ss"));
        return ChatMessageVO;
    }

    @Override
    public void deleteKey(String key, String id) {
        if (key.equals(CACHE_CHAT_HALL)) {
            redisTemplate.delete(key);
        } else {
            redisTemplate.delete(key + id);
        }
    }

    @Override
    public List<ChatMessageVO> getTeamChat(ChatRequest chatRequest, int chatType, User loginUser) {
        Long teamId = chatRequest.getTeamId();
        if (teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求有误");
        }
        List<ChatMessageVO> chatRecords = getCache(CACHE_CHAT_TEAM, String.valueOf(teamId));
        if (chatRecords != null) {
            List<ChatMessageVO> chatMessageVOS = checkIsMyMessage(loginUser, chatRecords);
            saveCache(CACHE_CHAT_TEAM, String.valueOf(teamId), chatMessageVOS);
            return chatMessageVOS;
        }
        Team team = teamService.getById(teamId);
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper.eq(Chat::getChatType, chatType).eq(Chat::getTeamId, teamId);
        List<ChatMessageVO> chatMessageVOS = returnMessage(loginUser, team.getUserId(), chatLambdaQueryWrapper);
        saveCache(CACHE_CHAT_TEAM, String.valueOf(teamId), chatMessageVOS);
        return chatMessageVOS;
    }

    @Override
    public List<ChatMessageVO> getHallChat(int chatType, User loginUser) {
        List<ChatMessageVO> chatRecords = getCache(CACHE_CHAT_HALL, String.valueOf(loginUser.getId()));
        if (chatRecords != null) {
            List<ChatMessageVO> chatMessageVOS = checkIsMyMessage(loginUser, chatRecords);
            saveCache(CACHE_CHAT_HALL, String.valueOf(loginUser.getId()), chatMessageVOS);
            return chatMessageVOS;
        }
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper.eq(Chat::getChatType, chatType);
        List<ChatMessageVO> chatMessageVOS = returnMessage(loginUser, null, chatLambdaQueryWrapper);
        saveCache(CACHE_CHAT_HALL, String.valueOf(loginUser.getId()), chatMessageVOS);
        return chatMessageVOS;
    }

    private List<ChatMessageVO> checkIsMyMessage(User loginUser, List<ChatMessageVO> chatRecords) {
        return chatRecords.stream().peek(chat -> {
            if (chat.getFormUser().getId() != loginUser.getId() && chat.getIsMy()) {
                chat.setIsMy(false);
            }
            if (chat.getFormUser().getId() == loginUser.getId() && !chat.getIsMy()) {
                chat.setIsMy(true);
            }
        }).collect(Collectors.toList());
    }

    private List<ChatMessageVO> returnMessage(User loginUser, Long userId, LambdaQueryWrapper<Chat> chatLambdaQueryWrapper) {
        List<Chat> chatList = this.list(chatLambdaQueryWrapper);
        return chatList.stream().map(chat -> {
            ChatMessageVO ChatMessageVO = chatResult(chat.getFromId(), chat.getText());
            boolean isCaptain = userId != null && userId.equals(chat.getFromId());
            if (userService.getById(chat.getFromId()).getRole() == ADMIN_ROLE || isCaptain) {
                ChatMessageVO.setIsAdmin(true);
            }
            if (chat.getFromId().equals(loginUser.getId())) {
                ChatMessageVO.setIsMy(true);
            }
            ChatMessageVO.setCreateTime(DateUtil.format(chat.getCreateTime(), "yyyy年MM月dd日 HH:mm:ss"));
            return ChatMessageVO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取私聊未读消息数量
     *
     * @param userId id
     * @return {@link Integer}
     */
    @Override
    public Integer getUnReadPrivateNum(Long userId) {
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper.eq(Chat::getToId, userId).eq(Chat::getChatType, PRIVATE_CHAT)
                .eq(Chat::getIsRead, 0);
        return Math.toIntExact(this.count(chatLambdaQueryWrapper));
    }

    /**
     * 获取私聊列表
     *
     * @param userId id
     * @return {@link List}<{@link UserVO}>
     */
    @Override
    public List<PrivateChatVO> getPrivateList(Long userId) {
        // 查询我发送的消息
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper.eq(Chat::getFromId, userId).eq(Chat::getChatType, PRIVATE_CHAT);
        List<Chat> mySend = this.list(chatLambdaQueryWrapper);
        HashSet<Long> userIdSet = new HashSet<>();
        mySend.forEach((chat) -> {
            Long toId = chat.getToId();
            userIdSet.add(toId);
        });
        chatLambdaQueryWrapper.clear();
        // 查询我接受到的消息条数
        chatLambdaQueryWrapper.eq(Chat::getToId, userId).eq(Chat::getChatType, PRIVATE_CHAT);
        List<Chat> myReceive = this.list(chatLambdaQueryWrapper);
        myReceive.forEach((chat) -> {
            Long fromId = chat.getFromId();
            userIdSet.add(fromId);
        });
        List<User> userList = userService.listByIds(userIdSet);
        return userList.stream().map((user) -> {
            PrivateChatVO privateChatVO = new PrivateChatVO();
            privateChatVO.setUserId(user.getId());
            privateChatVO.setUsername(user.getUsername());
            privateChatVO.setAvatarUrl(user.getAvatarUrl());
            Pair<String, Date> pair = getPrivateLastMessage(userId, user.getId());
            privateChatVO.setLastMessage(pair.getKey());
            privateChatVO.setLastMessageDate(pair.getValue());
            privateChatVO.setUnReadNum(getUnreadNum(userId, user.getId()));
            return privateChatVO;
        }).sorted().collect(Collectors.toList());
    }


    /**
     * 获取未读消息数量
     *
     * @param loginId  登录id
     * @param remoteId 遥远id
     * @return {@link Integer}
     */
    private Integer getUnreadNum(Long loginId, Long remoteId) {
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper.eq(Chat::getFromId, remoteId)
                .eq(Chat::getToId, loginId)
                .eq(Chat::getChatType, PRIVATE_CHAT)
                .eq(Chat::getIsRead, 0);
        return Math.toIntExact(this.count(chatLambdaQueryWrapper));
    }

    /**
     * 获取私聊最后一条消息信息
     *
     * @param loginId  登录id
     * @param remoteId 遥远id
     * @return {@link String}
     */
    private Pair<String, Date> getPrivateLastMessage(Long loginId, Long remoteId) {
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper
                .eq(Chat::getFromId, loginId)
                .eq(Chat::getToId, remoteId)
                .eq(Chat::getChatType, PRIVATE_CHAT)
                .orderBy(true, false, Chat::getCreateTime);
        List<Chat> chatList1 = this.list(chatLambdaQueryWrapper);
        chatLambdaQueryWrapper.clear();
        chatLambdaQueryWrapper.eq(Chat::getFromId, remoteId)
                .eq(Chat::getToId, loginId)
                .eq(Chat::getChatType, PRIVATE_CHAT)
                .orderBy(true, false, Chat::getCreateTime);
        List<Chat> chatList2 = this.list(chatLambdaQueryWrapper);
        if (chatList1.isEmpty() && chatList2.isEmpty()) {
            return new Pair<>("", null);
        }
        if (chatList1.isEmpty()) {
            return new Pair<>(chatList2.get(0).getText(), chatList2.get(0).getCreateTime());
        }
        if (chatList2.isEmpty()) {
            return new Pair<>(chatList1.get(0).getText(), chatList1.get(0).getCreateTime());
        }
        if (chatList1.get(0).getCreateTime().after(chatList2.get(0).getCreateTime())) {
            return new Pair<>(chatList1.get(0).getText(), chatList1.get(0).getCreateTime());
        } else {
            return new Pair<>(chatList2.get(0).getText(), chatList2.get(0).getCreateTime());
        }
    }

    /**
     * 阅读私聊消息
     *
     * @param loginId  登录id
     * @param remoteId 遥远id
     * @return {@link Boolean}
     */
    @Override
    public Boolean readPrivateMessage(Long loginId, Long remoteId) {
        LambdaUpdateWrapper<Chat> chatLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        chatLambdaUpdateWrapper.eq(Chat::getFromId, remoteId)
                .eq(Chat::getToId, loginId)
                .eq(Chat::getChatType, PRIVATE_CHAT)
                .set(Chat::getIsRead, 1);
        return this.update(chatLambdaUpdateWrapper);
    }
}




