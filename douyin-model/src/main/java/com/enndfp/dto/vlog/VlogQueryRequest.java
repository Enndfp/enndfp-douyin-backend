package com.enndfp.dto.vlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

/**
 * 短视频查询请求体
 *
 * @author Enndfp
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VlogQueryRequest implements Serializable {

    private Long userId;

    private String search = "";

    private Integer current;

    private Integer pageSize;

    private static final long serialVersionUID = 1L;
}