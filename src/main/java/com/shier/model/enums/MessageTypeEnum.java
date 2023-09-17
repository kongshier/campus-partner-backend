package com.shier.model.enums;

/**
 * 消息类型枚举
 *
 * @author Shier
 * @date 2023/06/22
 */
public enum MessageTypeEnum {
    /**
     * 博客就像
     */
    BLOG_LIKE(0, "博文点赞"),
    /**
     * 博客评论等
     */
    BLOG_COMMENT_LIKE(1, "博文评论点赞");

    /**
     * 价值
     */
    private int value;
    /**
     * 文本
     */
    private String text;

    /**
     * 消息类型枚举
     *
     * @param value 价值
     * @param text  文本
     */
    MessageTypeEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获得价值
     *
     * @return int
     */
    public int getValue() {
        return value;
    }

    /**
     * 设置值
     *
     * @param value 价值
     * @return {@link MessageTypeEnum}
     */
    public MessageTypeEnum setValue(int value) {
        this.value = value;
        return this;
    }

    /**
     * 得到文本
     *
     * @return {@link String}
     */
    public String getText() {
        return text;
    }

    /**
     * 设置文本
     *
     * @param text 文本
     * @return {@link MessageTypeEnum}
     */
    public MessageTypeEnum setText(String text) {
        this.text = text;
        return this;
    }
}
