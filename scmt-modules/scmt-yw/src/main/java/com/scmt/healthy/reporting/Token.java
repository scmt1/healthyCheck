package com.scmt.healthy.reporting;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Token {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "userId")
    private String userId;

    @ApiModelProperty(value = "token")
    private String loginId;

    @ApiModelProperty(value = "message")
    private String username;

    @ApiModelProperty(value = "message")
    private String orgId;

    @ApiModelProperty(value = "message")
    private String orgName;

    @ApiModelProperty(value = "message")
    private String areaId;

    @ApiModelProperty(value = "serviceSuccess")
    private String areaName;

    @ApiModelProperty(value = "systemErrorCode")
    private String orgType;

    @ApiModelProperty(value = "systemErrorCode")
    private String orgCode;

    @ApiModelProperty(value = "systemErrorCode")
    private String orgArea;

    @ApiModelProperty(value = "field10")
    private String field10;

    @ApiModelProperty(value = "ownAreaIds")
    private String[] ownAreaIds;

    @ApiModelProperty(value = "token")
    private String token;


}
