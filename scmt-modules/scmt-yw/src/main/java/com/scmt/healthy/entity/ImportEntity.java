package com.scmt.healthy.entity;

import com.scmt.healthy.utils.IsNeeded;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yzy
 */
@Data
public class ImportEntity {

    @IsNeeded
    @ApiModelProperty(value = "姓名")
    private String personName;

    @IsNeeded
    @ApiModelProperty(value = "性别")
    private String sex;

    @IsNeeded
    @ApiModelProperty(value = "证件号")
    private String idCard;

    @IsNeeded
    @ApiModelProperty(value = "出生日期")
    private String birth;

    @IsNeeded
    @ApiModelProperty(value = "年龄")
    private String age;

    @IsNeeded
    @ApiModelProperty(value = "婚姻状况")
    private String isMarry;

    @IsNeeded
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @IsNeeded
    @ApiModelProperty(value = "工种代码")
    private String workTypeCode;

    @IsNeeded
    @ApiModelProperty(value = "总工龄年数")
    private String workYear;

    @IsNeeded
    @ApiModelProperty(value = "总工龄月数")
    private String workMonth;

    @IsNeeded
    @ApiModelProperty(value = "接害工龄年数")
    private String exposureWorkYear;

    @IsNeeded
    @ApiModelProperty(value = "接害工龄月数")
    private String exposureWorkMonth;

    @IsNeeded
    @ApiModelProperty(value = "接害开始日期")
    private String exposureStartDate;

    @IsNeeded
    @ApiModelProperty(value = "监测类型")
    private String jcType;

    @IsNeeded
    @ApiModelProperty(value = "在岗状态编码")
    private String workStateCode;
}
