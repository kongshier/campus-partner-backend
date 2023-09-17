package com.shier.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 短信发送工具
 *
 * @author Shier
 * @date 2023/06/22
 */
public class SMSUtils {
    public static void sendMessage(String phoneNum,String code) {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "ak", "sk");
        IAcsClient client = new DefaultAcsClient(profile);
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNum);
        request.setSignName("sign");
        request.setTemplateCode("template");
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            SendSmsResponse response = client.getAcsResponse(request);
            System.out.println(response.getMessage());
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }
}