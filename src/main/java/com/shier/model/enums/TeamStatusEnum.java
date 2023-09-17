package com.shier.model.enums;

/**
 * 团队状态枚举
 *
 * @author Shier
 * @date 2023/06/22
 */
public enum TeamStatusEnum {
    /**
     * 公共
     */
    PUBLIC(0, "公开"),
    /**
     * 私人
     */
    PRIVATE(1, "私有"),
    /**
     * 秘密
     */
    SECRET(2, "加密");
    /**
     * value
     */
    private int value;

    /**
     * 文本
     */
    private String text;

    /**
     * 得到枚举值
     *
     * @param value 价值
     * @return {@link TeamStatusEnum}
     */
    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] values = TeamStatusEnum.values();
        for (TeamStatusEnum teamStatusEnum : values) {
            if (teamStatusEnum.getValue() == value) {
                return teamStatusEnum;
            }
        }
        return null;
    }

    /**
     * 团队状态枚举
     *
     * @param value 价值
     * @param text  文本
     */
    TeamStatusEnum(int value, String text) {
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
     * @return {@link TeamStatusEnum}
     */
    public TeamStatusEnum setValue(int value) {
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
     * @return {@link TeamStatusEnum}
     */
    public TeamStatusEnum setText(String text) {
        this.text = text;
        return this;
    }
}
