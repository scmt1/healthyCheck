package com.scmt.activiti.utils;

import com.scmt.activiti.vo.EmailMessage;
import com.scmt.core.common.constant.ActivitiConstant;
import com.scmt.core.common.constant.SettingConstant;
import com.scmt.core.common.exception.ScmtException;
import com.scmt.core.common.utils.EmailUtil;
import com.scmt.core.common.utils.SmsUtil;
import com.scmt.core.entity.MessageSend;
import com.scmt.core.entity.Setting;
import com.scmt.core.entity.User;
import com.scmt.core.service.MessageSendService;
import com.scmt.core.service.SettingService;
import com.scmt.core.service.UserService;
import com.scmt.core.vo.OtherSetting;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Exrickx
 */
@Transactional
@Component
@Slf4j
public class MessageUtil {

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSendService messageSendService;

    @Autowired
    private SettingService settingService;

    public OtherSetting getOtherSetting() {

        Setting setting = settingService.get(SettingConstant.OTHER_SETTING);
        if (StrUtil.isBlank(setting.getValue())) {
            throw new ScmtException("系统未配置访问域名");
        }
        return new Gson().fromJson(setting.getValue(), OtherSetting.class);
    }

    /**
     * 发送工作流消息
     * @param userId      发送用户
     * @param content     消息内容
     * @param sendMessage 是否发站内信息
     * @param sendSms     是否发短信
     * @param sendEmail   是否发邮件
     */
    @Async
    public void sendActMessage(String userId, String content, Boolean sendMessage, Boolean sendSms, Boolean sendEmail) {

        User user = userService.get(userId);
        if (user == null) {
            return;
        }
        MessageSend messageSend = new MessageSend();
        messageSend.setUserId(user.getId());
        if (sendMessage && ActivitiConstant.MESSAGE_TODO_CONTENT.equals(content)) {
            // 待办
            messageSend.setMessageId(ActivitiConstant.MESSAGE_TODO_ID);
            messageSendService.send(messageSend);
        } else if (sendMessage && ActivitiConstant.MESSAGE_PASS_CONTENT.equals(content)) {
            // 通过
            messageSend.setMessageId(ActivitiConstant.MESSAGE_PASS_ID);
            messageSendService.send(messageSend);
        } else if (sendMessage && ActivitiConstant.MESSAGE_BACK_CONTENT.equals(content)) {
            // 驳回
            messageSend.setMessageId(ActivitiConstant.MESSAGE_BACK_ID);
            messageSendService.send(messageSend);
        } else if (sendMessage && ActivitiConstant.MESSAGE_DELEGATE_CONTENT.equals(content)) {
            // 委托
            messageSend.setMessageId(ActivitiConstant.MESSAGE_DELEGATE_ID);
            messageSendService.send(messageSend);
        }
        if (StrUtil.isNotBlank(user.getMobile()) && sendSms) {
            try {
                smsUtil.sendActMessage(user.getMobile(), content);
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        if (StrUtil.isNotBlank(user.getEmail()) && sendEmail) {
            EmailMessage e = new EmailMessage();
            e.setUsername(user.getUsername());
            e.setContent(content);
            e.setFullUrl(getOtherSetting().getDomain());
            try {
                emailUtil.sendTemplateEmail(user.getEmail(), "【Scmt】工作流通知提醒", "act-message-email", e);
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        }
    }
}
