package com.shier.constants;

/**
 * 系统常量
 *
 * @author Shier
 * @date 2023/06/22
 */
public interface SystemConstants {
    /**
     * 页面大小
     */
    long PAGE_SIZE = 8;

    /**
     * 电子邮件发送邮箱
     */
    String EMAIL_FROM = "2927527234@qq.com";

    /**
     * 默认缓存页数
     */
    int DEFAULT_CACHE_PAGE = 5;

    /**
     * 最长登录空闲时间
     */
    public static final int MAXIMUM_LOGIN_IDLE_TIME = 900;

    /**
     * 默认缓冲区大小
     */
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    /**
     * 文件结束
     */
    public static final int FILE_END = -1;
}
