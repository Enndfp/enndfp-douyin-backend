package com.enndfp.dto.vlog;

import lombok.Data;

import java.io.Serializable;

/**
 * 短视频发布请求体
 *
 * @author Enndfp
 */
@Data
public class VlogPublishRequest implements Serializable {

    private Long vlogerId;

    private String url;

    private String cover;

    private String title;

    private Integer width;

    private Integer height;

    private static final long serialVersionUID = 1L;
}