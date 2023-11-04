package com.enndfp.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.pojo.Vlog;
import com.enndfp.vo.VlogVO;

import java.util.List;

/**
 * @author Enndfp
 */
public interface VlogService extends IService<Vlog> {

    /**
     * 发布短视频，保存入库
     *
     * @param vlogPublishRequest
     */
    void publish(VlogPublishRequest vlogPublishRequest);

    /**
     * 获取查询条件
     *
     * @param search
     * @return
     */
    LambdaQueryWrapper<Vlog> getQueryWrapper(String search);

}
