package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author dengjie
 * @since 2021-10-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TCareerHistory对象", description = "")
public class TCareerHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "起始日期")
    private Date startDate;
    @ApiModelProperty(value = "结束日期")
    private Date endDate;

    @ApiModelProperty(value = "工作单位")
    private String workUnit;

    @ApiModelProperty(value = "工种代码")
    private String workTypeCode;
    @ApiModelProperty(value = "工种名称")
    private String workTypeText;
    @ApiModelProperty(value = "危害因素代码")
    private String hazardFactorsCode;
    @ApiModelProperty(value = "危害因素名称")
    private String hazardFactorsText;

    @ApiModelProperty(value = "防护措施")
    private String protectiveMeasures;

    @ApiModelProperty(value = "人员主键")
    private String personId;

    @ApiModelProperty(value = "创建人")
    private String createId;
    @ApiModelProperty(value = "创建人")
    private String createName;
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "类型 1放射 2非放射")
    private Integer type;

    @ApiModelProperty(value = "日工作时数或工作量")
    private String workload;
    @ApiModelProperty(value = "职业史累积受照剂量")
    private String exposureDose;
    @ApiModelProperty(value = "职业史过量照射史")
    private String overexposure;
    @ApiModelProperty(value = "职业照射种类")
    private String irradiationType;
    @ApiModelProperty(value = "职业照射种类代码")
    private String irradiationTypeCode;
    @ApiModelProperty(value = "放射线种类")
    private String radiationType;
    @ApiModelProperty(value = "接触时间")
    private String contactTime;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "备注")
    private String department;

}
