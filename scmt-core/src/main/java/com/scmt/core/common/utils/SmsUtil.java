package com.scmt.core.common.utils;

import com.scmt.core.common.constant.SettingConstant;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.entity.Setting;
import com.scmt.core.service.SettingService;
import com.scmt.core.vo.SmsSetting;
import cn.hutool.core.util.StrUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Exrickx
 */
@Component
@Slf4j
public class SmsUtil {

    @Autowired
    private SettingService settingService;

    public SmsSetting getSmsSetting() {

        Setting setting = settingService.get(SettingConstant.ALI_SMS);
        if (StrUtil.isBlank(setting.getValue())) {
            throw new ScmtException("您还未配置阿里云短信");
        }
        return new Gson().fromJson(setting.getValue(), SmsSetting.class);
    }

    /**
     * 发送验证码 模版变量为 code
     * @param mobile
     * @param code
     * @param templateCode
     * @return
     * @throws ClientException
     */
    public void sendCode(String mobile, String code, String templateCode) {

        sendSms(mobile, "code", code, templateCode);
    }

    /**
     * 发送工作流消息 模版变量为 content
     * @param mobile
     * @param content
     * @return
     * @throws ClientException
     */
    public void sendActMessage(String mobile, String content) {

        // 获取工作流消息模板
        Setting setting = settingService.get(SettingConstant.ALI_SMS_ACTIVITI);
        sendSms(mobile, "content", content, setting.getValue());
    }

    /**
     * 发送短信
     * @param mobile       手机号
     * @param param        替换短信模板 变量
     * @param value        变量值
     * @param templateCode 短信模板code
     * @return
     * @throws ClientException
     */
    public void sendSms(String mobile, String param, String value, String templateCode) {

        SmsSetting s = getSmsSetting();

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", s.getAccessKey(), s.getSecretKey());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", s.getSignName());
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"" + param + "\":\"" + value + "\"}");

        CommonResponse response;
        try {
            response = client.getCommonResponse(request);
        } catch (ClientException e) {
            log.error(e.getMessage());
            throw new ScmtException("请求发送短信验证码失败，" + e.getErrMsg());
        }
        JsonObject result = JsonParser.parseString(response.getData()).getAsJsonObject();
        String code = result.get("Code").getAsString();
        String message = result.get("Message").getAsString();
        if (!"OK".equals(code) && !"OK".equals(message)) {
            throw new ScmtException("请求发送验证码失败，" + message);
        }
    }
}
