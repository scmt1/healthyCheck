package com.scmt.healthy.entity;

import com.scmt.healthy.utils.IsNeeded;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/***
 * 职业体检人员导入实体类
 */
@Data
public class ImportHealthyPersonEntity {
    @IsNeeded
    @ApiModelProperty(value = "姓名")
    private String personName;

    @IsNeeded
    @ApiModelProperty(value = "证件号")
    private String idCard;

    @IsNeeded
    @ApiModelProperty(value = "手机号")
    private String mobile;
}
