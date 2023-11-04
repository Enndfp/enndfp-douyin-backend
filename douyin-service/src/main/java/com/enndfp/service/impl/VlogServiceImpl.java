package com.enndfp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.common.ErrorCode;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.dto.vlog.VlogQueryRequest;
import com.enndfp.mapper.VlogMapper;
import com.enndfp.pojo.Vlog;
import com.enndfp.service.VlogService;
import com.enndfp.utils.RedisIdWorker;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.VlogVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.enndfp.constant.VlogConstants.DEFAULT_CURRENT;
import static com.enndfp.constant.VlogConstants.DEFAULT_PAGE_SIZE;

/**
 * @author Enndfp
 */
@Service
public class VlogServiceImpl extends ServiceImpl<VlogMapper, Vlog>
        implements VlogService {

    @Resource
    private VlogMapper vlogMapper;
    @Resource
    private RedisIdWorker redisIdWorker;

    @Override
    public void publish(VlogPublishRequest vlogPublishRequest) {
        // 1. 拷贝对象，填充基本信息
        Vlog vlog = BeanUtil.copyProperties(vlogPublishRequest, Vlog.class);
        long id = redisIdWorker.nextId("vlog");
        vlog.setId(id);

        // 2. 保存视频
        vlogMapper.insert(vlog);
    }

    @Override
    public Page<VlogVO> getIndexVlogList(VlogQueryRequest vlogQueryRequest) {
        Long userId = vlogQueryRequest.getUserId();
        String search = vlogQueryRequest.getSearch();
        Integer current = vlogQueryRequest.getCurrent();
        Integer pageSize = vlogQueryRequest.getPageSize();

        // 1. 构造分页器
        if (current == null) current = DEFAULT_CURRENT;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        Page<VlogVO> page = new Page<>(current, pageSize);

        // 2. 判断是否有搜索条件，封装map传递
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }
        return vlogMapper.getIndexVlogList(page, map);
    }

    @Override
    public VlogVO getVlogDetailById(Long userId, Long vlogId) {
        // 1. 构造条件map
        Map<String, Object> map = new HashMap<>();
        map.put("vlogId", vlogId);

        // 2. 执行查询详情逻辑
        VlogVO vlogVO = vlogMapper.getVlogDetailById(map);

        // 3. 判断是否存在视频
        ThrowUtils.throwIf(vlogVO == null, ErrorCode.VLOG_IS_NOT_EXIST);
        return vlogVO;
    }

    @Transactional
    @Override
    public void changeToPrivateOrPublic(Long userId, Long vlogId, Integer yesOrNo) {
        // 1. 构造条件查询器
        LambdaQueryWrapper<Vlog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vlog::getId, vlogId);
        queryWrapper.eq(Vlog::getVlogUserId, userId);

        // 2. 创建待执行的vlog对象
        Vlog vlog = new Vlog();
        vlog.setIsPrivate(yesOrNo);

        vlogMapper.update(vlog, queryWrapper);
    }

    @Override
    public Page<Vlog> queryMyVlogList(VlogQueryRequest vlogQueryRequest, Integer yesOrNo) {
        Long userId = vlogQueryRequest.getUserId();
        Integer current = vlogQueryRequest.getCurrent();
        Integer pageSize = vlogQueryRequest.getPageSize();

        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);

        // 2. 构造分页器
        if (current == null) current = DEFAULT_CURRENT;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        Page<Vlog> page = new Page<>(current, pageSize);

        // 3. 构造查询条件
        LambdaQueryWrapper<Vlog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Vlog::getVlogUserId,userId);
        queryWrapper.eq(Vlog::getIsPrivate,yesOrNo);

        return vlogMapper.selectPage(page,queryWrapper);
    }
}




