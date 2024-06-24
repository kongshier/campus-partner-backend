package com.shier.controller;

import com.shier.common.BaseResponse;
import com.shier.common.ErrorCode;
import com.shier.common.ResultUtils;
import com.shier.exception.BusinessException;
import com.shier.model.domain.Team;
import com.shier.model.domain.User;
import com.shier.service.FileService;
import com.shier.service.TeamService;
import com.shier.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.shier.constants.UserConstants.ADMIN_ROLE;

/**
 * @author Shier
 * 文件上传接口
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
@CrossOrigin(originPatterns = {"http://localhost:5173", "http://47.121.118.209", "http://localhost:5174"}, allowCredentials = "true")
public class FileController {

    @Resource
    private FileService fileService;

    @Resource
    private UserService userService;


    @Resource
    private TeamService teamService;

    /**
     * 上传
     *
     * @param file    文件
     * @param request 请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "file", value = "文件"),
                    @ApiImplicitParam(name = "request", value = "request请求")})
    public BaseResponse<String> upload(MultipartFile file, HttpServletRequest request) {
        if (file == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请上传文件");
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请登录");
        }

        // 上传到阿里云对象存储
        String fileUrl = fileService.uploadFileAvatar(file);

        User user = new User();
        user.setId(loginUser.getId());
        user.setAvatarUrl(fileUrl);
        boolean success = userService.updateById(user);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像上传失败");
        }
        return ResultUtils.success(fileUrl);
    }

    /**
     * 上传
     *
     * @param file    文件
     * @param request 请求
     * @return {@link BaseResponse}<{@link String}>
     */
    @PostMapping("/upload/team/{id}")
    @ApiOperation(value = "文件上传")
    @ApiImplicitParams(
            {@ApiImplicitParam(name = "id", value = "id"),
                    @ApiImplicitParam(name = "file", value = "文件"),
                    @ApiImplicitParam(name = "request", value = "request请求"),
            })
    public BaseResponse<Boolean> uploadTeam(@PathVariable Long id, MultipartFile file, HttpServletRequest request) {
        if (file == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (id == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        if (!loginUser.getRole().equals(ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        String uploadedFileTeamImage = fileService.uploadFileAvatar(file);
        Team team = new Team();
        team.setId(id);
        team.setCoverImage(uploadedFileTeamImage);
        teamService.updateById(team);
        return ResultUtils.success(true);
    }
}
