package com.wudao.service;

import com.wudao.entity.Banner;
import java.util.List;

public interface BannerService {
    List<Banner> getActiveBanners();
    Banner createBanner(Banner banner);
}
