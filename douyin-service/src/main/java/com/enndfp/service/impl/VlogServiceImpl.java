package com.enndfp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enndfp.common.ErrorCode;
import com.enndfp.dto.vlog.VlogPublishRequest;
import com.enndfp.dto.vlog.VlogQueryRequest;
import com.enndfp.mapper.MyLikedVlogMapper;
import com.enndfp.mapper.VlogMapper;
import com.enndfp.pojo.MyLikedVlog;
import com.enndfp.pojo.User;
import com.enndfp.pojo.Vlog;
import com.enndfp.service.UserService;
import com.enndfp.service.VlogService;
import com.enndfp.utils.RedisIdWorker;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.VlogVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.enndfp.constant.RedisConstants.*;
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
    @Resource
    private MyLikedVlogMapper myLikedVlogMapper;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtils redisUtils;

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
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);

        // 1. 构造分页器
        if (current == null) current = DEFAULT_CURRENT;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        Page<VlogVO> page = new Page<>(current, pageSize);

        // 2. 判断是否有搜索条件，封装map传递
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(search)) {
            map.put("search", search);
        }

        // 3. 处理后置参数
        Page<VlogVO> indexVlogList = vlogMapper.getIndexVlogList(page, map);
        // 3.1 得到具体数据
        List<VlogVO> vlogVOList = indexVlogList.getRecords();
        for (VlogVO vlogVO : vlogVOList) {
            Long vlogerId = vlogVO.getVlogerId();
            Long vlogId = vlogVO.getVlogId();

            // 3.2 判断当前用户是否点赞过视频
            vlogVO.setDoILikeThisVlog(doILikeVlog(userId, vlogId));
            // 3.3 获得当前视频被点赞过的总数
            vlogVO.setLikeCounts(this.getVlogBeLikedCounts(vlogId));
            // 3.3 判断用户是否关注该博主

        }

        return indexVlogList;
    }

    @Override
    public Page<VlogVO> getMyLikedVlogList(VlogQueryRequest vlogQueryRequest) {
        Long userId = vlogQueryRequest.getUserId();
        Integer current = vlogQueryRequest.getCurrent();
        Integer pageSize = vlogQueryRequest.getPageSize();
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);

        // 1. 构造分页器
        if (current == null) current = DEFAULT_CURRENT;
        if (pageSize == null) pageSize = DEFAULT_PAGE_SIZE;
        Page<VlogVO> page = new Page<>(current, pageSize);

        // 2. 判断是否有搜索条件，封装map传递
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        return vlogMapper.getMyLikedVlogList(page, map);
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
        queryWrapper.eq(Vlog::getVlogUserId, userId);
        queryWrapper.eq(Vlog::getIsPrivate, yesOrNo);

        return vlogMapper.selectPage(page, queryWrapper);
    }

    @Transactional
    @Override
    public void userLikeVlog(Long userId, Long vlogerId, Long vlogId) {
        // 1. 判断用户、博主和视频是否存在
        List<User> users = userService.listByIds(Arrays.asList(userId, vlogerId));
        for (User user : users) {
            ThrowUtils.throwIf(user == null, ErrorCode.USER_NOT_EXIST);
        }
        Vlog vlog = vlogMapper.selectById(vlogId);
        ThrowUtils.throwIf(vlog == null, ErrorCode.VLOG_IS_NOT_EXIST);

        // 2. 构造对象
        MyLikedVlog likedVlog = new MyLikedVlog();
        long id = redisIdWorker.nextId("likedVlog");
        likedVlog.setId(id);
        likedVlog.setUserId(userId);
        likedVlog.setVlogId(vlogId);

        // 3. 保存入库
        int result = myLikedVlogMapper.insert(likedVlog);
        ThrowUtils.throwIf(result != 1, ErrorCode.LIKE_FAILED);

        // 4. 保存成功后，视频和视频发布者的获赞都 +1
        redisUtils.increment(VLOG_LIKE_COUNTS_KEY + vlogId, 1);
        redisUtils.increment(VLOGER_LIKE_COUNTS_KEY + vlogerId, 1);

        // 5. 在 Redis 中保存我点赞的视频
        redisUtils.set(USER_LIKE_VLOG + userId + ":" + vlogId, "1");
    }

    @Transactional
    @Override
    public void userUnLikeVlog(Long userId, Long vlogerId, Long vlogId) {
        // 1. 构造条件删除器
        LambdaQueryWrapper<MyLikedVlog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MyLikedVlog::getUserId, userId);
        queryWrapper.eq(MyLikedVlog::getVlogId, vlogId);

        // 2. 逻辑删除数据
        int result = myLikedVlogMapper.delete(queryWrapper);
        ThrowUtils.throwIf(result != 1, ErrorCode.UNLIKE_FAILED);

        // 3. 删除成功后，视频和视频发布者的获赞都 -1
        redisUtils.decrement(VLOG_LIKE_COUNTS_KEY + vlogId, 1);
        redisUtils.decrement(VLOGER_LIKE_COUNTS_KEY + vlogerId, 1);

        // 4. 删除 Redis 中我点赞的视频
        redisUtils.del(USER_LIKE_VLOG + userId + ":" + vlogId);
    }

    @Override
    public Integer getVlogBeLikedCounts(Long vlogId) {
        String countsStr = redisUtils.get(VLOG_LIKE_COUNTS_KEY + vlogId);
        if (StringUtils.isBlank(countsStr)) {
            countsStr = "0";
        }
        return Integer.valueOf(countsStr);
    }

    /**
     * 查询当前用户是否点赞过该视频
     *
     * @param userId
     * @param vlogId
     * @return
     */
    private boolean doILikeVlog(Long userId, Long vlogId) {
        String doILike = redisUtils.get(USER_LIKE_VLOG + userId + ":" + vlogId);
        return StringUtils.isNotBlank(doILike);
    }
}




