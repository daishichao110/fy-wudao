package com.wudao.service.impl;

import com.wudao.common.SnowflakeIdWorker;
import com.wudao.entity.Banner;
import com.wudao.mapper.BannerMapper;
import com.wudao.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    @Override
    public List<Banner> getActiveBanners() {
        return bannerMapper.selectActiveBanners();
    }

    @Override
    public Banner createBanner(Banner banner) {
        if (!org.springframework.util.StringUtils.hasText(banner.getBannerId())) {
            banner.setBannerId(SnowflakeIdWorker.generateIdStr());
        }
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        if (banner.getSortOrder() == null) {
            banner.setSortOrder(0);
        }
        bannerMapper.insertBanner(banner);
        return banner;
    }
}
