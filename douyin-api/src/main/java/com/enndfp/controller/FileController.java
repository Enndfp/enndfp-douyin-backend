package com.enndfp.controller;

import cn.hutool.core.util.IdUtil;
import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.config.MinIOConfig;
import com.enndfp.dto.user.UserUploadRequest;
import com.enndfp.enums.FileTypeEnum;
import com.enndfp.service.UserService;
import com.enndfp.utils.MinIOUtils;
import com.enndfp.utils.ResultUtils;
import com.enndfp.utils.ThrowUtils;
import com.enndfp.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private MinIOConfig minIOConfig;
    @Resource
    private UserService userService;

    /**
     * 上传头像和背景图
     *
     * @param userUploadRequest
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<UserVO> upload(@ModelAttribute UserUploadRequest userUploadRequest, MultipartFile file) {
        Long userId = userUploadRequest.getUserId();
        Integer type = userUploadRequest.getType();

        // 1. 校验请求参数
        ThrowUtils.throwIf(userId == null, ErrorCode.PARAMS_ERROR);
        if (!FileTypeEnum.BGIMG.type.equals(type) && !FileTypeEnum.FACE.type.equals(type)) {
            ThrowUtils.throwException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        // 2. 获取原始文件名进行重命名
        String fileName = generateFileNameBasedOnType(type, file.getOriginalFilename());

        // 3. 上传文件
        try {
            MinIOUtils.uploadFile(minIOConfig.getBucketName(), fileName, file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4. 重命名图片地址
        String imgUrl = minIOConfig.getFileHost() + "/" + minIOConfig.getBucketName() + "/" + fileName;

        // 5. 处理上传逻辑
        UserVO userVO = userService.upload(userUploadRequest, imgUrl);

        return ResultUtils.success(userVO);
    }

    /**
     * 根据类型重命名文件
     *
     * @param type
     * @param originalFilename
     * @return
     */
    private String generateFileNameBasedOnType(Integer type, String originalFilename) {
        String prefixType = "";
        if (FileTypeEnum.BGIMG.type.equals(type)) {
            prefixType = "bg";
        } else if (FileTypeEnum.FACE.type.equals(type)) {
            prefixType = "face";
        }

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniquePart = "_" + IdUtil.fastSimpleUUID().substring(0, 8);
        return prefixType + uniquePart + suffix;
    }

}
