package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.service.AliyunOssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 阿里云文件与图片上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private AliyunOssService aliyunOssService;

    /**
     * 上传图片到阿里云服务器
     *
     * @param file 前端传递的文件
     * @param dir 可选：指定保存的文件夹目录（默认 "images/"）
     * @return 包含 relativePath (存数据库) 和 fullUrl (公网展示地址)
     */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dir", required = false, defaultValue = "images/") String dir) {
        log.info("[FileUploadController] 收到文件上传请求: fileName={}, size={} bytes, dir={}",
                file != null ? file.getOriginalFilename() : "null",
                file != null ? file.getSize() : 0,
                dir);
        if (file == null || file.isEmpty()) {
            log.warn("[FileUploadController] 上传失败: 上传文件为空");
            return Result.error("上传文件不能为空");
        }
        if (file.getSize() > 1024 * 1024) {
            log.warn("[FileUploadController] 上传失败: 文件大小限制1MB, 当前文件: {} bytes", file.getSize());
            return Result.error("上传图片大小不能超过 1MB，请压缩或选择较小的图片");
        }
        try {
            Map<String, String> uploadResult = aliyunOssService.uploadImage(file, dir);
            log.info("[FileUploadController] 文件上传至阿里云成功: relativePath={}, fullUrl={}",
                    uploadResult.get("relativePath"), uploadResult.get("fullUrl"));
            return Result.success(uploadResult);
        } catch (Exception e) {
            log.error("[FileUploadController] ❌ 上传至阿里云服务器失败", e);
            return Result.error("上传至阿里云服务器失败: " + e.getMessage());
        }
    }

    /**
     * 将数据库中存储的相对路径转换为完整的公网展示 URL
     *
     * @param relativePath 相对路径（例如 images/2026/09/xxx.jpg）
     * @return 完整的 URL 地址
     */
    @GetMapping("/full-url")
    public Result<String> getFullUrl(@RequestParam("relativePath") String relativePath) {
        String fullUrl = aliyunOssService.toFullUrl(relativePath);
        return Result.success(fullUrl);
    }
}
