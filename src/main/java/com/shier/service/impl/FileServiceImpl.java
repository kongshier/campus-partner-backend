package com.shier.service.impl;

import cn.hutool.core.date.DateTime;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.shier.common.ErrorCode;
import com.shier.exception.BusinessException;
import com.shier.service.FileService;
import com.shier.utils.FileAliOssUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * @author Shier
 * 阿里云对象存储实现类
 */
@Service
public class FileServiceImpl implements FileService {

    // 最大图片大小 2M
    private final long maxFileSize = 2 * 1024 * 1024;

    /**
     * 上传头像到OSS
     */
    @Override
    public String uploadFileAvatar(MultipartFile file) {
        // 工具类获取值
        String endpoint = FileAliOssUtils.END_POINT;
        String accessKeyId = FileAliOssUtils.KEY_ID;
        String accessKeySecret = FileAliOssUtils.KEY_SECRET;
        String bucketName = FileAliOssUtils.BUCKET_NAME;
        InputStream inputStream = null;
        try {
            // 创建OSS实例
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            // 判断文件大小
            long fileSize = file.getSize();
            if (fileSize > maxFileSize) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "文件不能大于2M");
            }

            // 把文件按照日期分类，获取当前日期
            String datePath = new DateTime().toString("yyyy-MM");
            // 获取上传文件的输入流
            inputStream = file.getInputStream();
            // 获取文件名称
            String originalFileName = file.getOriginalFilename();

            // 拼接日期和文件路径
            String fileName = datePath + "/" + originalFileName;

            // 判断文件是否存在
            boolean exists = ossClient.doesObjectExist(bucketName, fileName);
            if (exists) {
                // 如果文件已存在，则先删除原来的文件再进行覆盖
                ossClient.deleteObject(bucketName, fileName);
            }

            // 创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setContentType(getcontentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 调用oss实例中的方法实现上传
            // 参数1： Bucket名称
            // 参数2： 上传到oss文件路径和文件名称 /aa/bb/1.jpg
            // 参数3： 上传文件的输入流
            ossClient.putObject(bucketName, fileName, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            // 把上传后文件路径返回，需要把上传到阿里云oss路径手动拼接出来
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param filenameExtension 文件后缀
     * @return String
     */
    public static String getcontentType(String filenameExtension) {
        if ("bmp".equalsIgnoreCase(filenameExtension)) {
            return "image/bmp";
        }
        if ("gif".equalsIgnoreCase(filenameExtension)) {
            return "image/gif";
        }
        if ("jpeg".equalsIgnoreCase(filenameExtension) || filenameExtension.equalsIgnoreCase("jpg")
                || "png".equalsIgnoreCase(filenameExtension)) {
            return "image/jpeg";
        }
        if ("html".equalsIgnoreCase(filenameExtension)) {
            return "text/html";
        }
        if ("txt".equalsIgnoreCase(filenameExtension)) {
            return "text/plain";
        }
        if ("vsd".equalsIgnoreCase(filenameExtension)) {
            return "application/vnd.visio";
        }
        if ("pptx".equalsIgnoreCase(filenameExtension) || filenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if ("docx".equalsIgnoreCase(filenameExtension) || filenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if ("xml".equalsIgnoreCase(filenameExtension)) {
            return "text/xml";
        }
        return "application/octet-stream";
    }
}