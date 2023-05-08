package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *
 * </p>
 *
 * @author lbc
 * @since 2022-7-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TTestRecord对象", description="")
public class TTestRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "单位id")
    private String unitId;

    @ApiModelProperty(value = "危害因素编码")
    private String hazardFactors;

    @ApiModelProperty(value = "危害因素名称")
    private String hazardFactorsText;

    @ApiModelProperty(value = "其他危害因素名称")
    private String otherHazardFactors;

    @ApiModelProperty(value = "在岗状态编码")
    private String workStateCode;

    @ApiModelProperty(value = "在岗状态名称")
    private String workStateText;

    @ApiModelProperty(value = "工种名称")
    private String workTypeCode;

    @ApiModelProperty(value = "工种代码")
    private String workTypeText;

    @ApiModelProperty(value = "工种其他名称")
    private String workName;

    @ApiModelProperty(value = "浓度（强度）范围")
    private String strength;

    @ApiModelProperty(value = "检测时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date testTime;

    @ApiModelProperty(value = "删除标识（0-未删除，1-删除）")
    private Integer delFlag = 0;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
}
