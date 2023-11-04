package com.enndfp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.mapper.VlogMapper;
import com.enndfp.pojo.Vlog;
import com.enndfp.service.VlogService;
import com.enndfp.utils.RedisIdWorker;
import com.enndfp.vo.VlogVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public LambdaQueryWrapper<Vlog> getQueryWrapper(String search) {
        LambdaQueryWrapper<Vlog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(search), Vlog::getTitle, search);
        return queryWrapper;
    }
}




