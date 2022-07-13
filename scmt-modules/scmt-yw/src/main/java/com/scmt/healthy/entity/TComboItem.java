package com.scmt.healthy.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
 * @author mike
 * @since 2021-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TComboItem对象", description = "")
public class TComboItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "简称")
    @TableField(exist = false)
    private String shortName;

    @ApiModelProperty(value = "排序")
    @TableField(exist = false)
    private Float orderNum;

    @ApiModelProperty(value = "销售价（元）")
    @TableField(exist = false)
    private BigDecimal salePrice;

    @ApiModelProperty(value = "折扣(元)")
    private BigDecimal discount;

    @ApiModelProperty(value = "折扣价(元)")
    private BigDecimal discountPrice;

    @ApiModelProperty(value = "适合人群")
    @TableField(exist = false)
    private String suitableRange;

    @ApiModelProperty(value = "项目介绍")
    @TableField(exist = false)
    private String introduce;

    @ApiModelProperty(value = "检查地址")
    @TableField(exist = false)
    private String address;

    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private String remark;

    @ApiModelProperty(value = "是否有附件")
    @TableField(exist = false)
    private String isFile;

    @ApiModelProperty(value = "附件地址")
    @TableField(exist = false)
    private String url;


    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "所属部门")
    @TableField(exist = false)
    private String departmentId;

    @ApiModelProperty(value = "诊断模板")
    @TableField(exist = false)
    private String template;

    @ApiModelProperty(value = "服务类型")
    @TableField(exist = false)
    private String serviceType;

    @ApiModelProperty(value = "标本")
    @TableField(exist = false)
    private String specimen;

    @ApiModelProperty(value = "诊台是否显示")
    @TableField(exist = false)
    private String diagnostic;

    @ApiModelProperty(value = "套餐id")
    private String comboId;

    @ApiModelProperty(value = "组合项目id")
    private String portfolioProjectId;

    @ApiModelProperty(value = "科室id")
    @TableField(exist = false)
    private String officeId;

    @ApiModelProperty(value = "科室名称")
    @TableField(exist = false)
    private String officeName;

    @ApiModelProperty(value = "项目类型 1必检项目 2选检项目")
    private Integer projectType = 1;
}
