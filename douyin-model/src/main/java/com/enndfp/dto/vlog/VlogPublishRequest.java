package com.enndfp.dto.vlog;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 短视频发布请求体
 *
 * @author Enndfp
 */
@Data
public class VlogPublishRequest implements Serializable {

    @NotNull(message = "视频发布者不能为空")
    @Min(value = 1, message = "视频发布者ID必须是正整数")
    private Long vlogerId;

    @NotBlank(message = "视频播放地址不能为空")
    @Pattern(regexp = "^(http[s]?://)?[\\w-]+(\\.[\\w-]+)+[/#?]?.*$", message = "视频播放地址格式不正确")
    private String url;

    @NotBlank(message = "视频封面地址不能为空")
    @Pattern(regexp = "^(http[s]?://)?[\\w-]+(\\.[\\w-]+)+[/#?]?.*$", message = "视频封面地址格式不正确")
    private String cover;

    @NotBlank(message = "视频标题不能为空")
    @Size(min = 1, max = 256, message = "视频标题的长度必须在1到256个字符之间")
    private String title;

    @NotNull(message = "视频宽度不能为空")
    @Min(value = 1, message = "视频宽度必须是正整数")
    @Max(value = 1920, message = "视频宽度不能超过1920")
    private Integer width;

    @NotNull(message = "视频高度不能为空")
    @Min(value = 1, message = "视频高度必须是正整数")
    @Max(value = 1080, message = "视频高度不能超过1080")
    private Integer height;

    private static final long serialVersionUID = 1L;
}