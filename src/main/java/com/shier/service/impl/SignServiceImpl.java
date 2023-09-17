package com.shier.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shier.mapper.SignMapper;
import com.shier.model.domain.Sign;
import com.shier.service.SignService;
import org.springframework.stereotype.Service;

/**
 * @author Shier
 * @description 针对表【sign(签到表)】的数据库操作Service实现
 * @createDate 2023-09-16 20:28:39
 */
@Service
public class SignServiceImpl extends ServiceImpl<SignMapper, Sign>
        implements SignService {

}




