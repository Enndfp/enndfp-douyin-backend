package com.enndfp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.common.ErrorCode;
import com.enndfp.enums.MessageEnum;
import com.enndfp.enums.YesOrNo;
import com.enndfp.mapper.FansMapper;
import com.enndfp.pojo.Fans;
import com.enndfp.service.FansService;
import com.enndfp.service.MessageService;
import com.enndfp.utils.RedisIdWorker;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.FansVO;
import com.enndfp.vo.VlogerVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.enndfp.constant.RedisConstants.*;
import static com.enndfp.constant.PageConstants.DEFAULT_CURRENT;
import static com.enndfp.constant.PageConstants.DEFAULT_PAGE_SIZE;

/**
 * @author Enndfp
 */
@Service
public class FansServiceImpl extends ServiceImpl<FansMapper, Fans>
        implements FansService {

    @Resource
    private FansMapper fansMapper;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private MessageService messageService;

    @Transactional
    @Override
    public void doFollow(Long fanId, Long vlogerId) {
        // 1. 构造fans对象
        Fans fans = new Fans();
        long id = redisIdWorker.nextId("fans");
        fans.setId(id);
        fans.setFanId(fanId);
        fans.setVlogerId(vlogerId);

        // 2. 查询双方是否互相关注
        Fans vloger = this.queryIsMutualFan(vlogerId, fanId);
        // 2.1 如果vloger也关注了fans，则更新关系
        if (vloger != null) {
            fans.setIsMutualFan(YesOrNo.YES.type);
            vloger.setIsMutualFan(YesOrNo.YES.type);
            int result1 = fansMapper.updateById(vloger);
            ThrowUtils.throwIf(result1 != 1, ErrorCode.FOLLOW_FAILED);
        } else {
            fans.setIsMutualFan(YesOrNo.NO.type);
        }

        // 3. 插入fans关注信息
        int result2 = fansMapper.insert(fans);
        ThrowUtils.throwIf(result2 != 1, ErrorCode.FOLLOW_FAILED);

        // 4. 保存成功之后，我的关注+1，博主的粉丝+1
        redisUtils.increment(MY_FOLLOWS_COUNTS_KEY + fanId, 1);
        redisUtils.increment(MY_FANS_COUNTS_KEY + vlogerId, 1);

        // 5. 我和博主的关联关系，依赖redis，不要存储数据库，避免db的性能瓶颈
        redisUtils.set(FANS_AND_VLOGER_RELATIONSHIP_KEY + fanId + ":" + vlogerId, "1");

        // 6. 发送系统消息：关注
        messageService.createMsg(fanId, vlogerId, MessageEnum.FOLLOW_YOU.type, null);
    }

    @Transactional
    @Override
    public void doCancel(Long fanId, Long vlogerId) {
        // 1. 判断双方是否是互相关注，如果是，则取消关系
        Fans fan = this.queryIsMutualFan(fanId, vlogerId);
        ThrowUtils.throwIf(fan == null, ErrorCode.NOT_FOLLOW);
        if (fan.getIsMutualFan().equals(YesOrNo.YES.type)) {
            // 1.1 删除双方互相关注信息
            Fans vloger = this.queryIsMutualFan(vlogerId, fanId);
            vloger.setIsMutualFan(YesOrNo.NO.type);
            fansMapper.updateById(vloger);
        }

        // 2. 删除自己的关注信息
        int result = fansMapper.deleteById(fan);
        ThrowUtils.throwIf(result != 1, ErrorCode.CANCEL_FOLLOW_FAILED);

        // 3. 删除成功之后，我的关注-1，博主的粉丝-1
        redisUtils.decrement(MY_FOLLOWS_COUNTS_KEY + fanId, 1);
        redisUtils.decrement(MY_FANS_COUNTS_KEY + vlogerId, 1);

        // 4. 删除我和博主的关联关系
        redisUtils.del(FANS_AND_VLOGER_RELATIONSHIP_KEY + fanId + ":" + vlogerId);
    }

    @Override
    public boolean queryDoIFollowVloger(Long fanId, Long vlogerId) {
        // 从 Redis 中查询，减少数据库压力
        String result = redisUtils.get(FANS_AND_VLOGER_RELATIONSHIP_KEY + fanId + ":" + vlogerId);
        return result != null;
    }

    @Override
    public Page<VlogerVO> queryMyFollows(Long fanId, Integer current, Integer pageSize) {
        // 1. 构造分页器
        if (current == null) current = DEFAULT_CURRENT;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        Page<VlogerVO> page = new Page<>(current, pageSize);

        // 2. 添加查询条件，封装map传递
        Map<String, Object> map = new HashMap<>();
        map.put("fanId", fanId);

        return fansMapper.queryMyFollows(page, map);
    }

    @Override
    public Page<FansVO> queryMyFans(Long vlogerId, Integer current, Integer pageSize) {
        // 1. 构造分页器
        if (current == null) current = DEFAULT_CURRENT;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        Page<FansVO> page = new Page<>(current, pageSize);

        // 2. 添加查询条件，封装map传递
        Map<String, Object> map = new HashMap<>();
        map.put("vlogerId", vlogerId);

        return fansMapper.queryMyFans(page, map);
    }

    /**
     * 查询是否是共同的粉丝
     *
     * @param fanId
     * @param vlogerId
     * @return
     */
    private Fans queryIsMutualFan(Long fanId, Long vlogerId) {
        // 1. 构造条件器
        LambdaQueryWrapper<Fans> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Fans::getFanId, fanId);
        queryWrapper.eq(Fans::getVlogerId, vlogerId);

        // 2. 执行查询逻辑
        return fansMapper.selectOne(queryWrapper);
    }
}




