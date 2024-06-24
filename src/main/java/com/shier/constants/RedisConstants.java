package com.shier.constants;

/**
 * Redis常量
 *
 * @author Shier
 * @date 2023/06/22
 */
public interface RedisConstants {
    String LOGIN_USER_KEY = "campus:login:token:";

    Long LOGIN_USER_TTL = 300L;
    /**
     * 注册验证码键
     */
    String REGISTER_CODE_KEY = "campus:register:";
    /**
     * 注册验证码过期时间
     */
    Long REGISTER_CODE_TTL = 15L;
    /**
     * 用户更新电话键
     */
    String USER_UPDATE_PHONE_KEY = "suer:user:update:phone:";
    /**
     * 用户更新电话过期时间
     */
    Long USER_UPDATE_PHONE_TTL = 30L;
    /**
     * 用户更新邮件键
     */
    String USER_UPDATE_EMAIL_KEY = "suer:user:update:email:";
    /**
     * 用户更新邮件过期时间
     */
    Long USER_UPDATE_EMAIl_TTL = 15L;
    /**
     * 用户忘记密码键
     */
    String USER_FORGET_PASSWORD_KEY = "campus:user:forget:";
    /**
     * 用户忘记密码过期时间
     */
    Long USER_FORGET_PASSWORD_TTL = 15L;
    /**
     * 博客推送键
     */
    String BLOG_FEED_KEY = "campus:feed:blog:";
    /**
     * 新博文消息键
     */
    String MESSAGE_BLOG_NUM_KEY = "campus:message:blog:num:";
    /**
     * 新点赞消息键
     */
    String MESSAGE_LIKE_NUM_KEY = "campus:message:like:num:";
    /**
     * 用户推荐缓存
     */
    String USER_RECOMMEND_KEY = "campus:recommend:";

    /**
     * 新点赞消息键
     */
    String MESSAGE_FRIENDS_NUM_KEY = "campus:message:friends:num:";
}
