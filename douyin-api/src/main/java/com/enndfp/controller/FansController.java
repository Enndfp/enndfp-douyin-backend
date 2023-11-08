package com.enndfp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.pojo.User;
import com.enndfp.service.FansService;
import com.enndfp.service.UserService;
import com.enndfp.utils.RedisUtils;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.FansVO;
import com.enndfp.vo.VlogerVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static com.enndfp.constant.RedisConstants.FANS_AND_VLOGER_RELATIONSHIP_KEY;

/**
 * @author Enndfp
 */
@Slf4j
@RestController
@RequestMapping("/fans")
public class FansController {

    @Resource
    private FansService fansService;
    @Resource
    private UserService userService;
    @Resource
    private RedisUtils redisUtils;

    @PostMapping("/follow")
    public BaseResponse<?> follow(@RequestParam Long fanId,
                                  @RequestParam Long vlogerId) {
        // 1. 判断请求参数
        ThrowUtils.throwIf(fanId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogerId == null, ErrorCode.PARAMS_ERROR);

        // 2. 判断当前用户不能关注自己
        ThrowUtils.throwIf(fanId.equals(vlogerId), ErrorCode.NOT_ALLOWED_FOLLOW_YOURSELF);

        // 3. 判断两个id的用户是否存在
        List<User> users = userService.listByIds(Arrays.asList(fanId, vlogerId));
        for (User user : users) {
            ThrowUtils.throwIf(user == null, ErrorCode.USER_NOT_EXIST);
        }

        // 4. 保存粉丝关系到数据库
        fansService.doFollow(fanId, vlogerId);
        return ResultUtils.success();
    }

    @PostMapping("/cancel")
    public BaseResponse<?> cancel(@RequestParam Long fanId,
                                  @RequestParam Long vlogerId) {
        // 1. 判断请求参数
        ThrowUtils.throwIf(fanId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogerId == null, ErrorCode.PARAMS_ERROR);

        // 2. 判断两个id的用户是否存在
        List<User> users = userService.listByIds(Arrays.asList(fanId, vlogerId));
        for (User user : users) {
            ThrowUtils.throwIf(user == null, ErrorCode.USER_NOT_EXIST);
        }

        // 3. 删除粉丝关系到数据库
        fansService.doCancel(fanId, vlogerId);
        return ResultUtils.success();
    }

    @GetMapping("/queryDoIFollowVloger")
    public BaseResponse<Boolean> queryDoIFollowVloger(@RequestParam Long fanId,
                                                      @RequestParam Long vlogerId) {
        // 1. 判断请求参数
        ThrowUtils.throwIf(fanId == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(vlogerId == null, ErrorCode.PARAMS_ERROR);

        // 2. 判断两个id的用户是否存在
        List<User> users = userService.listByIds(Arrays.asList(fanId, vlogerId));
        for (User user : users) {
            ThrowUtils.throwIf(user == null, ErrorCode.USER_NOT_EXIST);
        }

        // 3. 判断是否关注
        return ResultUtils.success(fansService.queryDoIFollowVloger(fanId, vlogerId));
    }

    @GetMapping("/queryMyFollows")
    public BaseResponse<Page<VlogerVO>> queryMyFollows(@RequestParam Long fanId,
                                                       @RequestParam Integer current,
                                                       @RequestParam Integer pageSize) {
        // 1. 判断请求参数
        ThrowUtils.throwIf(fanId == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询逻辑
        Page<VlogerVO> vlogerVOPage = fansService.queryMyFollows(fanId, current, pageSize);

        return ResultUtils.success(vlogerVOPage);
    }

    @GetMapping("/queryMyFans")
    public BaseResponse<Page<FansVO>> queryMyFans(@RequestParam Long vlogerId,
                                                  @RequestParam Integer current,
                                                  @RequestParam Integer pageSize) {
        // 1. 判断请求参数
        ThrowUtils.throwIf(vlogerId == null, ErrorCode.PARAMS_ERROR);
        // 2. 处理分页查询逻辑
        Page<FansVO> fansVOPage = fansService.queryMyFans(vlogerId, current, pageSize);

        // 3. 查询 Redis 判断是否互关，设置互关状态
        for (FansVO fansVO : fansVOPage.getRecords()) {
            String relationship = redisUtils.get(
                    FANS_AND_VLOGER_RELATIONSHIP_KEY + vlogerId + ":" + fansVO.getFanId());
            if (StringUtils.isNotBlank(relationship)) {
                fansVO.setFriend(true);
            }
        }

        return ResultUtils.success(fansVOPage);
    }


}
