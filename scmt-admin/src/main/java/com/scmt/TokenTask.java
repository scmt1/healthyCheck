package com.scmt;

import com.scmt.healthy.reporting.EmploymentUpload;
import com.scmt.healthy.service.ITTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 定时获取从业体检网报上传token
 */
@Component
@EnableScheduling
public class TokenTask {

    @Autowired
    private ITTokenService tokenService;

    @Autowired
    private EmploymentUpload employmentUpload;


    @Scheduled(cron = "0 0 7 * * ?")
    @PostConstruct
    private void session(){
        try {
            if (employmentUpload.getToken()){
                tokenService.getToken();
            }
        }catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
    }

}
