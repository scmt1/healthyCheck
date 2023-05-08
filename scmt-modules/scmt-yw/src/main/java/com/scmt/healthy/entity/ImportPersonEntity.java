package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.scmt.healthy.utils.IsNeeded;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/***
 * 职业体检人员导入实体类
 */
@Data
public class ImportPersonEntity {
    @IsNeeded
    @ApiModelProperty(value = "姓名")
    private String personName;

    @IsNeeded
    @ApiModelProperty(value = "证件号")
    private String idCard;

    @IsNeeded
    @ApiModelProperty(value = "手机号")
    private String mobile;

    @IsNeeded
    @ApiModelProperty(value = "危害因素")
    private String hazardFactorsText;
    @IsNeeded
    @ApiModelProperty(value = "其他危害因素名称")
    private String otherHazardFactors;

    @IsNeeded
    @ApiModelProperty(value = "工种代码名称")
    private String workTypeText;

    @IsNeeded
    @ApiModelProperty(value = "工种其他名称")
    private String workName;

    @IsNeeded
    @ApiModelProperty(value = "在岗状态名称")
    private String workStateText;

    @IsNeeded
    @ApiModelProperty(value = "部门")
    private String department;

    @IsNeeded
    @ApiModelProperty(value = "文化程度")
    private String education;

    @ApiModelProperty(value = "婚姻状况")
    private String isMarry;

    @ApiModelProperty(value = "总工龄年数")
    private Integer workYear;

    @ApiModelProperty(value = "总工龄月数")
    private Integer workMonth;

    @ApiModelProperty(value = "接害工龄年数")
    private Integer exposureWorkYear;

    @ApiModelProperty(value = "接害工龄月数")
    private Integer exposureWorkMonth;

    @ApiModelProperty(value = "用工单位")
    private String dept;

    @ApiModelProperty(value = "错误信息")
    private String errorTxt;

}
