package com.enndfp.exception;

import com.enndfp.common.BaseResponse;
import com.enndfp.common.ErrorCode;
import com.enndfp.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * @author Enndfp
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 自定义异常捕获
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 方法参数校验异常捕获
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("methodArgumentNotValidExceptionHandler: ", e);
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = getErrors(bindingResult);
        return ResultUtils.error(ErrorCode.FAILED, errorMap);
    }

    /**
     * 限制文件大小异常捕获
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public BaseResponse<?> maxUploadSizeExceededExceptionHandler(MaxUploadSizeExceededException e) {
        log.error("maxUploadSizeExceededException: ", e);
        return ResultUtils.error(ErrorCode.FILE_MAX_SIZE_2MB_ERROR);
    }

    /**
     * 唯一约束异常捕获
     *
     * @param e
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public BaseResponse<?> duplicateKeyExceptionHandler(DuplicateKeyException e) {
        log.error("duplicateKeyException: ", e);
        return ResultUtils.error(ErrorCode.ALREADY_FOLLOW);
    }

    /**
     * 运行时异常捕获
     *
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException: ", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

    /**
     * 从方法参数校验异常中获取详细异常信息
     *
     * @param bindingResult
     * @return
     */
    private Map<String, String> getErrors(BindingResult bindingResult) {

        Map<String, String> errorMap = new HashMap<>();
        List<FieldError> errorList = bindingResult.getFieldErrors();

        for (FieldError error : errorList) {
            // 错误所对应的属性字段名
            String field = error.getField();
            // 错误所对应的信息
            String message = error.getDefaultMessage();
            errorMap.put(field, message);
        }
        return errorMap;
    }
}
