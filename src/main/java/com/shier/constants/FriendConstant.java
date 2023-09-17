package com.shier.constants;
/**
 * 好友常量
 *
 * @author Shier
 * @date 2023/06/22
 */
public interface FriendConstant {
    /**
     * 默认状态 未处理
     */
    int DEFAULT_STATUS = 0;
    /**
     * 已同意
     */
    int AGREE_STATUS = 1;
    /**
     * 已过期
     */
    int EXPIRED_STATUS = 2;

    /**
     * 撤销
     */
    int REVOKE_STATUS = 3;
    /**
     * 未读
     */
    int NOT_READ = 0;

    /**
     * 已读
     */
    int READ = 1;
}