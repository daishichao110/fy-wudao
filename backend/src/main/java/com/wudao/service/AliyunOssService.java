package com.wudao.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.wudao.config.AliyunOssProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 阿里云 OSS 服务实现类
 */
@Service
public class AliyunOssService {

    @Autowired
    private AliyunOssProperties ossProperties;

    /**
     * 上传图片到阿里云 OSS
     *
     * @param file 接收的文件
     * @param dirPrefix 目录前缀（如 "images/", "avatars/", "banners/" 等）
     * @return 包含相对路径 relativePath 和全路径 fullUrl 的 Map
     */
    public Map<String, String> uploadImage(MultipartFile file, String dirPrefix) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 整理目录前缀：比如 "images/"
        String dir = StringUtils.hasText(dirPrefix) ? dirPrefix.trim() : "images/";
        if (!dir.endsWith("/")) {
            dir += "/";
        }

        // 补充按年月划分的二级子目录（例如 images/2026/09/）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/");
        String datePath = sdf.format(new Date());

        // 获取原始文件后缀
        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 组合唯一的相对路径 (Object Key)
        String relativePath = dir + datePath + UUID.randomUUID().toString().replace("-", "") + extension;

        // 创建 OSSClient
        OSS ossClient = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );

        try {
            InputStream inputStream = file.getInputStream();
            // 上传文件至 OSS
            ossClient.putObject(ossProperties.getBucketName(), relativePath, inputStream);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        // 拼接公网访问 URL
        String fullUrl = toFullUrl(relativePath);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("relativePath", relativePath);
        resultMap.put("fullUrl", fullUrl);
        return resultMap;
    }

    /**
     * 将数据库保存的相对路径转换为完整的外网访问全路径 URL
     *
     * @param path 存储的相对路径或已经包含 http/https 的地址
     * @return 可访问的全路径地址
     */
    public String toFullUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        if (path.startsWith("http://") || path.startsWith("https://") || path.startsWith("data:")) {
            return path;
        }
        String prefix = ossProperties.getUrlPrefix();
        if (!StringUtils.hasText(prefix)) {
            prefix = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/";
        } else if (!prefix.endsWith("/")) {
            prefix += "/";
        }

        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        return prefix + cleanPath;
    }

    /**
     * 从全路径 URL 截取出相对路径 (Object Key)，以便存入数据库
     *
     * @param fullUrl 完整的 URL
     * @return 相对路径 key
     */
    public String toRelativePath(String fullUrl) {
        if (!StringUtils.hasText(fullUrl)) {
            return "";
        }
        String prefix = ossProperties.getUrlPrefix();
        if (StringUtils.hasText(prefix) && fullUrl.startsWith(prefix)) {
            return fullUrl.substring(prefix.length());
        }
        // 如果包含服务节点或域名，自动截取包含 dir 的后缀
        if (fullUrl.contains(".aliyuncs.com/")) {
            return fullUrl.substring(fullUrl.indexOf(".aliyuncs.com/") + 14);
        }
        return fullUrl;
    }
}
