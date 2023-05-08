package com.scmt.healthy.serviceimpl;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.scmt.healthy.entity.TEmploymentToken;
import com.scmt.healthy.mapper.TTokenMapper;
import com.scmt.healthy.reporting.EmploymentUpload;
import com.scmt.healthy.service.ITTokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.scmt.healthy.utils.HttpClient.sendPostRequestMh;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dengjie
 * @since 2023-04-07
 */
@Service
public class TTokenServiceImpl extends ServiceImpl<TTokenMapper, TEmploymentToken> implements ITTokenService {

    @Autowired
    private ITTokenService tokenService;

    @Autowired
    private EmploymentUpload employmentUpload;

    @Override
    public Boolean getToken() {
        TEmploymentToken tToken = new TEmploymentToken();
        long time = System.currentTimeMillis();
        String personId = "177cf201a6997f4ca0b8da298e31e3f1";
        String userMd5 = new Digester(DigestAlgorithm.MD5).digestHex(personId + employmentUpload.getHieAppKey() + time + employmentUpload.getHieAdapter());
        String userUrl = employmentUpload.getReportingIp() + "/adapter/http/ehrc/login/integration?hie_event_code=177cf201a6997f4ca0b8da298e31e3f1&hie_app_key=" + employmentUpload.getHieAppKey() + "&hie_time_stamp=" + time + "&hie_secret=" + userMd5;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("ehrkey", "ehr#health@check");
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("username", employmentUpload.getUsername());
        userMap.put("password", employmentUpload.getPassword());
        String token = sendPostRequestMh(userUrl, userMap, headers);
        List<TEmploymentToken> list = tokenService.list();
        if (list.size() > 0 && list!=null){
            String id = list.get(0).getId();
            tToken.setId(id);
            tToken.setToken(token);
            tToken.setCreateTime(new Date());
        }else {
            tToken.setToken(token);
            tToken.setCreateTime(new Date());
        }
        boolean res = tokenService.saveOrUpdate(tToken);
        return res;
    }
}
