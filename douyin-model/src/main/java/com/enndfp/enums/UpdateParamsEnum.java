package com.enndfp.enums;

import com.enndfp.common.ErrorCode;
import com.enndfp.utils.ThrowUtils;

import java.util.Arrays;

/**
 * 修改用户信息参数枚举
 *
 * @author Enndfp
 */
public enum UpdateParamsEnum {
    NICKNAME(1, "昵称"),
    DOUYINNUM(2, "抖音号"),
    SEX(3, "性别"),
    BIRTHDAY(4, "生日"),
    LOCATION(5, "所在地"),
    DESC(6, "个人简介");

    public final Integer type;
    public final String value;

    UpdateParamsEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 校验修改信息参数是否合法
     *
     * @param type
     */
    public static void validUpdateParamType(Integer type) {
        if (Arrays.stream(UpdateParamsEnum.values())
                .noneMatch(updateParam -> updateParam.type.equals(type))) {
            ThrowUtils.throwException(ErrorCode.UPDATED_PARAMS_ERROR);
        }
    }
}
