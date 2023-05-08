package com.scmt.healthy.reporting;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
public class Reporting {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "data")
    private String data;

    @ApiModelProperty(value = "code")
    private String code;

    @ApiModelProperty(value = "message")
    private String message;


    @ApiModelProperty(value = "success")
    private String success;

    @ApiModelProperty(value = "requestId")
    private String requestId;

    @ApiModelProperty(value = "datasourceIndex")
    private String datasourceIndex;

    @ApiModelProperty(value = "serviceSuccess")
    private String serviceSuccess;

    @ApiModelProperty(value = "systemErrorCode")
    private String systemErrorCode;
}
