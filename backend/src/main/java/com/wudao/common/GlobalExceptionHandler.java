package com.wudao.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

 private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

 @ExceptionHandler(IllegalArgumentException.class)
 public Result<String> handleIllegalArgument(IllegalArgumentException e) {
 log.warn("[GlobalExceptionHandler] Business Validation Warning: {}", e.getMessage());
 return Result.error(400, e.getMessage());
 }

 @ExceptionHandler(IllegalStateException.class)
 public Result<String> handleIllegalState(IllegalStateException e) {
 log.warn("[GlobalExceptionHandler] Business State Warning: {}", e.getMessage());
 return Result.error(400, e.getMessage());
 }

 @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
 public Result<String> handleMaxUploadSizeExceeded(org.springframework.web.multipart.MaxUploadSizeExceededException e) {
 log.warn("[GlobalExceptionHandler] 文件上传超出 1MB 限制: {}", e.getMessage());
 return Result.error(400, "上传图片大小不能超过 1MB，请压缩或选择较小的图片");
 }

 @ExceptionHandler(Exception.class)
 public Result<String> handleException(Exception e) {
 log.error("[GlobalExceptionHandler] Uncaught System Exception:", e);
 return Result.error(500, "系统响应异常: " + (e.getMessage() != null ? e.getMessage() : "服务器内部错误"));
 }
}
