package com.scmt.healthy.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
 * @author ycy
 * @since 2021-09-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TPortfolioProject对象", description = "")
public class TPortfolioProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "排序")
    private Float orderNum;

    @ApiModelProperty(value = "父节点ID")
    private String parentId;

    @ApiModelProperty(value = "科室id")
    private String officeId;

    @ApiModelProperty(value = "科室名称")
    private String officeName;

    @ApiModelProperty(value = "原价（元）")
    private BigDecimal marketPrice;

    @ApiModelProperty(value = "销售价（元）")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "成本价（元）")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "适合人群")
    private String suitableRange;

    @ApiModelProperty(value = "项目介绍")
    private String introduce;

    @ApiModelProperty(value = "检查地址")
    private String address;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;

    @ApiModelProperty(value = "所属部门")
    private String departmentId;

    @ApiModelProperty(value = "诊断模板")
    private String template;

    @ApiModelProperty(value = "服务类型")
    private String serviceType;

    @ApiModelProperty(value = "标本")
    private String specimen;

    @ApiModelProperty(value = "诊台显示")
    private String diagnostic;

    @ApiModelProperty(value = "是否为附件")
    private String isFile;

    @ApiModelProperty(value = "附件地址")
    private String url;

    @ApiModelProperty(value = "下级数据")
    @TableField(exist = false)
    private List<TPortfolioProject> children = new ArrayList<>();

    @ApiModelProperty(value = "已绑定项目")
    @TableField(exist = false)
    private List<TBaseProject> projectList = new ArrayList<>();

    @ApiModelProperty(value = "折扣")
    @TableField(exist = false)
    private String discount;

    @ApiModelProperty(value = "折扣价")
    @TableField(exist = false)
    private String discountPrice;

    @ApiModelProperty(value = "lis标本")
    private String lisSpecimen;

    @ApiModelProperty(value = "lis标本名字")
    private String lisSpecimenName;

    @ApiModelProperty(value = "lis系统对应Id")
    private String lisId;

    @ApiModelProperty(value = "lis系统对应编码")
    private String lisCode;

    @ApiModelProperty(value = "部位名称")
    private String deptName;

}
