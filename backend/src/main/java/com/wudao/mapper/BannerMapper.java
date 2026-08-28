package com.wudao.mapper;

import com.wudao.entity.Banner;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface BannerMapper {
    List<Banner> selectActiveBanners();
    int insertBanner(Banner banner);
}
