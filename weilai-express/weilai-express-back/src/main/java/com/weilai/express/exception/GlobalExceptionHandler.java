package com.weilai.express.exception;

import com.weilai.express.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数验证失败：{}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数绑定失败：{}", message);
        return Result.error(400, message);
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常：{} - {}", e.getCode(), e.getMessage());
        // 确保错误信息正确编码
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        e.printStackTrace(); // 强制打印完整堆栈到控制台
        
        // 在控制台输出更详细的信息
        System.err.println("=== 详细错误信息 ===");
        System.err.println("异常类型：" + e.getClass().getName());
        System.err.println("异常消息：" + e.getMessage());
        e.printStackTrace(System.err);
        System.err.println("===================");
        
        return Result.error("系统繁忙，请稍后再试");
    }
}
