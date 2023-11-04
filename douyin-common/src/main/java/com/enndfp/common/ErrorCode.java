package com.enndfp.common;

/**
 * 错误码
 *
 * @author Enndfp
 */
public enum ErrorCode {
    FAILED(500, "操作失败！"),


    PARAMS_ERROR(501, "请求参数错误！"),
    PHONE_IS_NULL(502, "手机号为空！"),
    PHONE_ILLEGALITY(503, "手机号不合法！"),
    SMS_SEND_FAILED(504, "验证码发送失败，请稍后重试！"),
    SMS_IS_NULL(505, "验证码为空！"),
    SMS_IS_EXPIRED_OR_NOT_MATCH(506, "验证码过期或不匹配，请稍后再试！"),
    USER_NOT_EXIST(507, "用户不存在！"),
    UPDATED_PARAMS_ERROR(508, "用户信息修改参数错误！"),
    DOUYINNUM_IS_EXIST(509, "抖音号已经存在！"),
    DOUYINNUM_NOT_UPDATE(510, "抖音号无法修改！"),
    USER_INFO_UPDATE_ERROR(511, "用户信息更新失败，请联系管理员！"),
    FILE_UPLOAD_FAILED(512, "文件上传失败！"),
    FILE_MAX_SIZE_2MB_ERROR(513, "仅支持2MB大小以下的图片上传！"),
    VLOG_PARAMS_ERROR(514, "视频参数错误！"),
    VLOG_IS_NOT_EXIST(515, "视频不存在！"),



    UN_LOGIN(501, "请登录后再继续操作！"),
    TICKET_INVALID(502, "会话失效，请重新登录！"),
    SMS_NEED_WAIT_ERROR(505, "短信发送太快啦~请稍后再试！"),
    USER_FROZEN(507, "用户已被冻结，请联系管理员！"),

    USER_INACTIVE_ERROR(509, "请前往[账号设置]修改信息激活后再进行后续操作！"),
    USER_INFO_UPDATED_NICKNAME_EXIST_ERROR(5092, "昵称已经存在！"),


    FILE_UPLOAD_NULL_ERROR(510, "文件不能为空，请选择一个文件再上传！"),

    FILE_FORMATTER_FAILED(512, "文件图片格式不支持！"),
    FILE_MAX_SIZE_500KB_ERROR(5131, "仅支持500kb大小以下的图片上传！"),

    FILE_NOT_EXIST_ERROR(514, "你所查看的文件不存在！"),
    USER_STATUS_ERROR(515, "用户状态参数出错！"),


    // 自定义系统级别异常 54x
    SYSTEM_INDEX_OUT_OF_BOUNDS(541, "系统错误，数组越界！"),
    SYSTEM_ARITHMETIC_BY_ZERO(542, "系统错误，无法除零！"),
    SYSTEM_NULL_POINTER(543, "系统错误，空指针！"),
    SYSTEM_NUMBER_FORMAT(544, "系统错误，数字转换异常！"),
    SYSTEM_PARSE(545, "系统错误，解析异常！"),
    SYSTEM_IO(546, "系统错误，IO输入输出异常！"),
    SYSTEM_FILE_NOT_FOUND(547, "系统错误，文件未找到！"),
    SYSTEM_CLASS_CAST(548, "系统错误，类型强制转换错误！"),
    SYSTEM_PARSER_ERROR(549, "系统错误，解析出错！"),
    SYSTEM_DATE_PARSER_ERROR(550, "系统错误，日期解析出错！"),

    // admin 管理系统 56x
    ADMIN_USERNAME_NULL_ERROR(561, "管理员登录名不能为空！"),
    ADMIN_USERNAME_EXIST_ERROR(562, "管理员登录名已存在！"),
    ADMIN_NAME_NULL_ERROR(563, "管理员负责人不能为空！"),
    ADMIN_PASSWORD_ERROR(564, "密码不能为空后者两次输入不一致！"),
    ADMIN_CREATE_ERROR(565, "添加管理员失败！"),
    ADMIN_PASSWORD_NULL_ERROR(566, "密码不能为空！"),
    ADMIN_NOT_EXIT_ERROR(567, "管理员不存在或密码错误！"),
    ADMIN_FACE_NULL_ERROR(568, "人脸信息不能为空！"),
    ADMIN_FACE_LOGIN_ERROR(569, "人脸识别失败，请重试！"),
    CATEGORY_EXIST_ERROR(570, "文章分类已存在，请换一个分类名！"),

    // 媒体中心 相关错误 58x
    ARTICLE_COVER_NOT_EXIST_ERROR(580, "文章封面不存在，请选择一个！"),
    ARTICLE_CATEGORY_NOT_EXIST_ERROR(581, "请选择正确的文章领域！"),
    ARTICLE_CREATE_ERROR(582, "创建文章失败，请重试或联系管理员！"),
    ARTICLE_QUERY_PARAMS_ERROR(583, "文章列表查询参数错误！"),
    ARTICLE_DELETE_ERROR(584, "文章删除失败！"),
    ARTICLE_WITHDRAW_ERROR(585, "文章撤回失败！"),
    ARTICLE_REVIEW_ERROR(585, "文章审核出错！"),
    ARTICLE_ALREADY_READ_ERROR(586, "文章重复阅读！"),

    // 人脸识别错误代码
    FACE_VERIFY_TYPE_ERROR(600, "人脸比对验证类型不正确！"),
    FACE_VERIFY_LOGIN_ERROR(601, "人脸登录失败！"),

    // 系统错误，未预期的错误 555
    SYSTEM_ERROR(555, "系统繁忙，请稍后再试！"),
    SYSTEM_OPERATION_ERROR(556, "操作失败，请重试或联系管理员"),
    SYSTEM_RESPONSE_NO_INFO(557, ""),
    SYSTEM_ERROR_GLOBAL(558, "全局降级：系统繁忙，请稍后再试！"),
    SYSTEM_ERROR_FEIGN(559, "客户端Feign降级：系统繁忙，请稍后再试！"),
    SYSTEM_ERROR_ZUUL(560, "请求系统过于繁忙，请稍后再试！");

    // 响应状态码
    private final Integer code;

    // 响应消息
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
