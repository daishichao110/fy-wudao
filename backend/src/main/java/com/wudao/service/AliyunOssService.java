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

 // 整理目录前缀：引入可配置的根目录（如 wudao/）
 String rootDir = ossProperties.getRootDir();
 String dir = StringUtils.hasText(dirPrefix) ? dirPrefix.trim() : "images/";
 if (dir.startsWith("/")) {
 dir = dir.substring(1);
 }
 if (!dir.endsWith("/")) {
 dir += "/";
 }
 if (StringUtils.hasText(rootDir)) {
 if (!rootDir.endsWith("/")) {
 rootDir += "/";
 }
 if (rootDir.startsWith("/")) {
 rootDir = rootDir.substring(1);
 }
 if (!dir.startsWith(rootDir)) {
 dir = rootDir + dir;
 }
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
 // 设置 Content-Type 元信息（避免渲染变成强制下载）
 com.aliyun.oss.model.ObjectMetadata metadata = new com.aliyun.oss.model.ObjectMetadata();
 if (extension.equalsIgnoreCase(".png")) {
 metadata.setContentType("image/png");
 } else if (extension.equalsIgnoreCase(".gif")) {
 metadata.setContentType("image/gif");
 } else if (extension.equalsIgnoreCase(".webp")) {
 metadata.setContentType("image/webp");
 } else {
 metadata.setContentType("image/jpeg");
 }

 // 上传文件至 OSS
 ossClient.putObject(ossProperties.getBucketName(), relativePath, inputStream, metadata);
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
     * 将数据库保存的相对路径转换为带有 Expires, OSSAccessKeyId, Signature 签名的授权访问全路径 URL
     *
     * @param path 存储的相对路径或已经包含 http/https 的地址
     * @return 带有预签名授权参数的可访问 URL 地址
     */
    public String toFullUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return "";
        }
        // 如果已经是自带 Signature 的预签名地址或 base64/本地静态资源，直接原样返回
        if (path.contains("Signature=") || path.contains("OSSAccessKeyId=") || path.startsWith("data:")) {
            return path;
        }

        String key = toRelativePath(path);
        if (!StringUtils.hasText(key)) {
            return "";
        }

        OSS ossClient = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );

        try {
            // 设置 10 年长期预签名授权有效期（满足前端长期免鉴权流畅访问）
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
            java.net.URL url = ossClient.generatePresignedUrl(ossProperties.getBucketName(), key, expiration);
            String presignedUrl = url.toString();

            // 如果配置了自定义 CDN 域名前缀（如 https://oss.52ddup.com/），替换默认 endpoint 域名
            String customPrefix = ossProperties.getUrlPrefix();
            if (StringUtils.hasText(customPrefix)) {
                if (!customPrefix.endsWith("/")) {
                    customPrefix += "/";
                }
                String defaultHost = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/";
                String httpHost = "http://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/";
                if (presignedUrl.startsWith(defaultHost)) {
                    presignedUrl = customPrefix + presignedUrl.substring(defaultHost.length());
                } else if (presignedUrl.startsWith(httpHost)) {
                    presignedUrl = customPrefix + presignedUrl.substring(httpHost.length());
                }
            }

            return presignedUrl;
        } catch (Exception e) {
            // 降级为直接拼接
            String prefix = ossProperties.getUrlPrefix();
            if (!StringUtils.hasText(prefix)) {
                prefix = "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/";
            } else if (!prefix.endsWith("/")) {
                prefix += "/";
            }
            return prefix + key;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 从全路径 URL 截取出相对路径 (Object Key)，以便存入数据库
     *
     * @param fullUrl 完整的 URL
     * @return 相对路径 key (裁剪掉 ?Signature=... 签名参数)
     */
    public String toRelativePath(String fullUrl) {
        if (!StringUtils.hasText(fullUrl)) {
            return "";
        }
        String url = fullUrl;

        // 裁剪掉 Signature、Expires 等 Query 参数
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }

        String prefix = ossProperties.getUrlPrefix();
        if (StringUtils.hasText(prefix) && url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        if (url.contains(".aliyuncs.com/")) {
            return url.substring(url.indexOf(".aliyuncs.com/") + 14);
        }
        return url;
    }
}
