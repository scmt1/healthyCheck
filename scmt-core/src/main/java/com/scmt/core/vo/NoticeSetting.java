package com.scmt.core.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Exrickx
 */
@Data
public class NoticeSetting implements Serializable {

    @ApiModelProperty(value = "公告开关")
    private Boolean open;

    @ApiModelProperty(value = "展示页面")
    private String position;

    @ApiModelProperty(value = "展示时长")
    private Integer duration;

    @ApiModelProperty(value = "公告标题")
    private String title;

    @ApiModelProperty(value = "公告内容")
    private String content;
}
