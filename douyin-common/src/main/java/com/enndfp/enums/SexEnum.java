package com.enndfp.enums;

/**
 * 用户性别枚举类
 *
 * @author Enndfp
 */
public enum SexEnum {
    woman(0, "女"),
    man(1, "男"),
    secret(2, "保密");

    private final Integer type;
    private final String value;

    SexEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
