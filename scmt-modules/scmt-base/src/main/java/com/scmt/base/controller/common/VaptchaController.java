package com.scmt.base.controller.common;

import com.scmt.core.common.constant.SettingConstant;
import com.scmt.core.common.redis.RedisTemplateHelper;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpUtil;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Api(description = "Vaptcha验证码离线验证接口")
@RequestMapping("/scmt/common/vaptcha")
@RestController
@Slf4j
public class VaptchaController {

    public static final String CHAR = "0123456789abcdef";

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @RequestMapping(value = "/offline", method = RequestMethod.GET)
    @ApiOperation(value = "vaptcha离线模式接口")
    public Object vaptchaOffline(String offline_action, String vid, String knock, String callback, String v,
                                 HttpServletResponse response) {

        response.setHeader("X-Content-Type-Options", "");
        // 获取offline_key
        String offline_key = redisTemplate.get(vid);
        if (StrUtil.isBlank(offline_key)) {
            // 校验是否进入离线模式
            String offCheck = HttpUtil.get(SettingConstant.CHANNEL_URL + vid);
            int offline_state = JsonParser.parseString(offCheck).getAsJsonObject().get("offline_state").getAsInt();
            if (offline_state == 0) {
                return "Vapthca未进入离线模式";
            } else {
                offline_key = JsonParser.parseString(offCheck).getAsJsonObject().get("offline_key").getAsString();
                redisTemplate.set(vid, offline_key, 3L, TimeUnit.MINUTES);
            }
        }
        if ("get".equals(offline_action)) {
            // 获取验证图
            String imageId = new Digester(DigestAlgorithm.MD5).digestHex(offline_key + getRandomStr());
            if (StrUtil.isBlank(knock)) {
                knock = IdUtil.simpleUUID();
            }
            redisTemplate.set(knock, imageId, 3L, TimeUnit.MINUTES);
            // 拼接并返回
            String result = callback + "({\"code\": \"" + SettingConstant.VALIDATE_SUCCESS + "\", \"imgid\": \""
                    + imageId + "\", \"knock\": \"" + knock + "\"})";
            return result;
        } else {
            String imageId = redisTemplate.get(knock);
            redisTemplate.delete(knock);
            if (StrUtil.isBlank(imageId)) {
                String result = callback + "({\"code\": \"" + SettingConstant.VALIDATE_FAIL + "\", \"msg\": \"knock过期\", \"token\": \"\"})";
                return result;
            }
            String validatekey = new Digester(DigestAlgorithm.MD5).digestHex(v + imageId);
            String offValidate = HttpUtil.get(SettingConstant.VALIDATE_URL + offline_key + "/" + validatekey);
            Boolean validateResult = JsonParser.parseString(offValidate).getAsJsonObject().get("result").getAsBoolean();
            String token, result;
            if (validateResult) {
                // 校验成功则生成token
                String uuid = IdUtil.simpleUUID();
                redisTemplate.set(knock, uuid, 3L, TimeUnit.MINUTES);
                token = SettingConstant.OFFLINE_MODE + knock + uuid;
                result = callback + "({\"code\": \"" + SettingConstant.VALIDATE_SUCCESS + "\", \"msg\": \"success\", \"token\": \"" + token + "\"})";
            } else {
                result = callback + "({\"code\": \"" + SettingConstant.VALIDATE_FAIL + "\", \"msg\": \"fail\", \"token\": \"\"})";
            }
            return result;
        }
    }

    public static String getRandomStr() {

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(CHAR.charAt(random.nextInt(16)));
        }
        return sb.toString();
    }
}
