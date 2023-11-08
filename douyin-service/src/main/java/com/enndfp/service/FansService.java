package com.enndfp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.enndfp.dto.vlog.VlogQueryRequest;
import com.enndfp.pojo.Fans;
import com.enndfp.vo.FansVO;
import com.enndfp.vo.VlogVO;
import com.enndfp.vo.VlogerVO;

/**
 * @author Enndfp
 */
public interface FansService extends IService<Fans> {

    /**
     * 关注
     *
     * @param fanId
     * @param vlogerId
     */
    void doFollow(Long fanId, Long vlogerId);

    /**
     * 取关
     *
     * @param fanId
     * @param vlogerId
     */
    void doCancel(Long fanId, Long vlogerId);

    /**
     * 查询用户是否关注博主
     *
     * @param fanId
     * @param vlogerId
     * @return
     */
    boolean queryDoIFollowVloger(Long fanId, Long vlogerId);

    /**
     * 查询我的关注列表
     *
     * @param fanId
     * @param current
     * @param pageSize
     * @return
     */
    Page<VlogerVO> queryMyFollows(Long fanId, Integer current, Integer pageSize);

    /**
     * 查询我的粉丝列表
     *
     * @param vlogerId
     * @param current
     * @param pageSize
     * @return
     */
    Page<FansVO> queryMyFans(Long vlogerId, Integer current, Integer pageSize);


}
