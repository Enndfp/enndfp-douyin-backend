package com.enndfp.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 用户上传图片请求体
 *
 * @author Enndfp
 */
@Data
public class UserUploadRequest implements Serializable {

    private Long userId;

    private Integer type;

    private static final long serialVersionUID = 4586537003425887549L;
}
