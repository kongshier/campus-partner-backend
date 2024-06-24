package com.shier.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shier.mapper.ConfigMapper;
import com.shier.model.domain.Config;
import com.shier.service.ConfigService;
import com.shier.service.FileService;
import com.shier.utils.FileUtils;
import com.shier.utils.SuperProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shier
 * @description 针对表【config】的数据库操作Service实现
 * @createDate 2024-06-18 18:31:25
 */
@Service
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config>
        implements ConfigService {

    @Resource
    private SuperProperties superProperties;

    @Resource
    private FileService fileService;


    @Value("${server.servlet.session.cookie.domain}")
    private String host;

    @Value("${server.port}")
    private String port;

    @Override
    public String getNoticeTest() {
        LambdaQueryWrapper<Config> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(Config::getType, 0);
        Config config = this.getOne(configLambdaQueryWrapper);
        if (config == null) {
            return null;
        }
        return config.getValue();
    }

    @Override
    public List<String> getSwiperImgs() {
        LambdaQueryWrapper<Config> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(Config::getType, 1);
        List<Config> list = this.list(configLambdaQueryWrapper);
        if (list.isEmpty()) {
            return null;
        }
        return list.stream().map(Config::getValue).collect(Collectors.toList());
    }

    @Override
    public void updateNoticeText(String text) {
        LambdaQueryWrapper<Config> configLambdaQueryWrapper = new LambdaQueryWrapper<>();
        configLambdaQueryWrapper.eq(Config::getType, 0);
        this.remove(configLambdaQueryWrapper);
        Config config = new Config();
        config.setType(0);
        config.setValue(text);
        this.save(config);
    }

    @Override
    public void uploadImages(MultipartFile file) {
        if (superProperties.isUseLocalStorage()) {
            String fileName = FileUtils.uploadFile2Local(file);
            String fileUrl = "http://" + host + ":" + port + "/api/common/image/" + fileName;
            Config config = new Config();
            config.setValue(fileUrl);
            config.setType(1);
            this.save(config);
        } else {
            // 上传到阿里云OSS
            String filePath = fileService.uploadFileAvatar(file);
            Config config = new Config();
            config.setValue(filePath);
            config.setType(1);
            this.save(config);
        }
    }

    @Override
    public void removeUrl(String url) {
        LambdaUpdateWrapper<Config> configLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        configLambdaUpdateWrapper.eq(Config::getType, 1).eq(Config::getValue, url);
        this.remove(configLambdaUpdateWrapper);
    }
}





