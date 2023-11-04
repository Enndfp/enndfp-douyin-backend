package com.enndfp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.dto.vlog.VlogQueryRequest;
import com.enndfp.pojo.Vlog;
import com.enndfp.vo.VlogVO;

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
     * 分页查询视频带搜索功能
     *
     * @param vlogQueryRequest
     * @return
     */
    Page<VlogVO> getIndexVlogList(VlogQueryRequest vlogQueryRequest);

    /**
     * 从搜索页点击查看vlog详情
     *
     * @param userId
     * @param vlogId
     * @return
     */
    VlogVO getVlogDetailById(Long userId, Long vlogId);

    /**
     * 把视频改为公开/私密
     *
     * @param userId
     * @param vlogId
     * @param yesOrNo
     */
    void changeToPrivateOrPublic(Long userId, Long vlogId, Integer yesOrNo);

    /**
     * 分页查询公开/私密的视频列表
     *
     * @param vlogQueryRequest
     * @param yesOrNo
     * @return
     */
    Page<Vlog> queryMyVlogList(VlogQueryRequest vlogQueryRequest, Integer yesOrNo);

}
