package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.Banner;
import com.wudao.service.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    private static final Logger log = LoggerFactory.getLogger(BannerController.class);

    @Autowired
    private BannerService bannerService;

    @Autowired
    private com.wudao.service.AliyunOssService aliyunOssService;

    @GetMapping("/list")
    public Result<List<Banner>> getBanners() {
        log.info("[REST API GET /api/banner/list] Fetching active performance & showcase banners");
        List<Banner> list = bannerService.getActiveBanners();
        if (list != null) {
            for (Banner banner : list) {
                if (org.springframework.util.StringUtils.hasText(banner.getImageUrl())) {
                    banner.setImageUrl(aliyunOssService.toFullUrl(banner.getImageUrl()));
                }
            }
        }
        return Result.success("获取Banner列表成功", list);
    }

    @PostMapping("/publish")
    public Result<Banner> publishBanner(@RequestBody Banner banner) {
        log.info("[REST API POST /api/banner/publish] Publishing new event banner: title={}, badge={}, creator={}",
                banner.getTitle(), banner.getBadge(), banner.getCreatorName());
        
        if (banner.getTitle() == null || banner.getTitle().trim().isEmpty()) {
            return Result.error("活动名称/标题不能为空");
        }
        if (banner.getImageUrl() == null || banner.getImageUrl().trim().isEmpty()) {
            // 设置默认优雅精美配图
            banner.setImageUrl("/image/banner1.jpg");
        }
        if (banner.getBadge() == null || banner.getBadge().trim().isEmpty()) {
            banner.setBadge("🎪 大型演出");
        }

        Banner created = bannerService.createBanner(banner);
        return Result.success("成功发布演出风采活动展播", created);
    }
}
