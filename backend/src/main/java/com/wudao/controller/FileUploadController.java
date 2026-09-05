package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.service.AliyunOssService;
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
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        try {
            Map<String, String> uploadResult = aliyunOssService.uploadImage(file, dir);
            return Result.success(uploadResult);
        } catch (Exception e) {
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
