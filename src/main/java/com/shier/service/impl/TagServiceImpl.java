package com.shier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shier.mapper.TagMapper;
import com.shier.model.domain.Tag;
import com.shier.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author Shier
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2023-05-07 19:05:01
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}




