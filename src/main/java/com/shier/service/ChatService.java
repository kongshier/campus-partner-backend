package com.shier.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shier.model.domain.Chat;
import com.shier.model.domain.User;
import com.shier.model.request.ChatRequest;
import com.shier.model.vo.ChatMessageVO;
import com.shier.model.vo.PrivateChatVO;

import java.util.Date;
import java.util.List;

/**
* @author Shier
* @description 针对表【chat(聊天消息表)】的数据库操作Service
* @createDate 2023-06-17 21:50:15
*/
public interface ChatService extends IService<Chat> {
     List<ChatMessageVO> getPrivateChat(ChatRequest chatRequest, int chatType, User loginUser);

     List<ChatMessageVO> getCache(String redisKey, String id);

     void saveCache(String redisKey, String id, List<ChatMessageVO> chatMessageVos);

     ChatMessageVO chatResult(Long userId, Long toId, String text, Integer chatType, Date createTime);

     void deleteKey(String key, String id);

    List<ChatMessageVO> getTeamChat(ChatRequest chatRequest, int teamChat, User loginUser);

    List<ChatMessageVO> getHallChat(int chatType, User loginUser);

    // 获得到私聊用户列表
    List<PrivateChatVO> getPrivateList(Long id);

    Integer getUnReadPrivateNum(Long userId);
    // 私聊已读
    Boolean readPrivateMessage(Long loginId, Long remoteId);
}
