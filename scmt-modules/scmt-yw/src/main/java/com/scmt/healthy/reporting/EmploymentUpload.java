package com.scmt.healthy.reporting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Socket配置类型
 *  @author dengjie
 */
@Component
public class EmploymentUpload {
    /**
     *通用hie_app_key
     */
    @Value("${reporting.hieAppKey}")
    private String hieAppKey;

    /**
     *秘钥字符串
     */
    @Value("${reporting.hieAdapter}")
    private String hieAdapter;

    /**
     *请求Ip
     */
    @Value("${reporting.reportingIp}")
    private String reportingIp;

    /**
     *从业体检上传账号
     */
    @Value("${reporting.username}")
    private String username;

    /**
     *发证单位
     */
    @Value("${reporting.name}")
    private String name;

    /**
     *发证单位
     */
    @Value("${reporting.isToken}")
    private Boolean isToken;

    public Boolean getToken() {
        return isToken;
    }

    public void setToken(Boolean token) {
        isToken = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    /**
     *机构自增码
     */
    @Value("${reporting.registration}")
    private String registration;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     *从业体检上传密码
     */
    @Value("${reporting.password}")
    private String password;

    public String getHieAppKey() {
        return hieAppKey;
    }

    public void setHieAppKey(String hieAppKey) {
        this.hieAppKey = hieAppKey;
    }

    public String getHieAdapter() {
        return hieAdapter;
    }

    public void setHieAdapter(String hieAdapter) {
        this.hieAdapter = hieAdapter;
    }

    public String getReportingIp() {
        return reportingIp;
    }

    public void setReportingIp(String reportingIp) {
        this.reportingIp = reportingIp;
    }
}
