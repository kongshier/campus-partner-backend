package com.shier.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shier.model.domain.Config;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 配置服务
 *
 * @description 针对表【config】的数据库操作Service
 * @createDate 2024-04-08 11:53:41
 * @date 2024/04/12
 */
public interface ConfigService extends IService<Config> {

    /**
     * 获取通知测试
     *
     * @return {@link String}
     */
    String getNoticeTest();

    /**
     * 获取刷卡器imgs
     *
     * @return {@link List}<{@link String}>
     */
    List<String> getSwiperImgs();

    /**
     * 更新通知文本
     *
     * @param text 文本
     */
    void updateNoticeText(String text);

    /**
     * 上传图像
     *
     * @param file 文件
     */
    void uploadImages(MultipartFile file);

    /**
     * 删除url
     *
     * @param url url
     */
    void removeUrl(String url);
}
