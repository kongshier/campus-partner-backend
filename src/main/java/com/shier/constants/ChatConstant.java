package com.shier.constants;

/**
 * 聊天常量
 *
 * @author Shier
 * @date 2023/06/22
 */
public interface ChatConstant {

    /**
     * 私聊
     */
    int PRIVATE_CHAT = 1;

    /**
     * 队伍群聊
     */

    int TEAM_CHAT = 2;
    /**
     * 大厅聊天
     */
    int HALL_CHAT = 3;

    /**
     * 缓存聊天大厅
     */
    String CACHE_CHAT_HALL = "supper:chat:chat_records:chat_hall";

    /**
     * 缓存私人聊天
     */
    String CACHE_CHAT_PRIVATE = "campus:chat:chat_records:chat_private:";

    /**
     * 缓存聊天团队
     */
    String CACHE_CHAT_TEAM = "campus:chat:chat_records:chat_team:";

}
