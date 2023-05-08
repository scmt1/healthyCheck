package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author mike
 * @since 2021-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TCombo对象", description="")
public class TCombo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "套餐名称")
    private String name;

    @ApiModelProperty(value = "封面图片地址")
    private String url;

    @ApiModelProperty(value = "套餐类别")
    private String type;

    @ApiModelProperty(value = "简拼")
    private String simpleSpell;

    @ApiModelProperty(value = "适合性别")
    private String fitSex;

    @ApiModelProperty(value = "排序")
    private Integer orderNum;

    @ApiModelProperty(value = "套餐介绍")
    private String remark;

    @ApiModelProperty(value = "删除标识(0-未删除，1-已删除)")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    @ApiModelProperty(value = "套餐项目")
    @TableField(exist = false)
    private List<TComboItem> comboItemList;

    @ApiModelProperty(value = "危害因素")
    private String hazardFactors;

    @ApiModelProperty(value = "危害因素")
    private String hazardFactorsText;

    @ApiModelProperty(value = "职业阶段")
    private String careerStage;

    @ApiModelProperty(value = "职业病")
    private String occupationalDiseases;

    @ApiModelProperty(value = "职业禁忌症")
    private String occupationalTaboo;

    @ApiModelProperty(value = "职业病代码")
    private String occupationalDiseasesCode;

    @ApiModelProperty(value = "职业禁忌症代码")
    private String occupationalTabooCode;

    @ApiModelProperty(value = "体检机构id")
    private String checkOrgId;

    @ApiModelProperty(value = "组合项目名称集")
    @TableField(exist = false)
    private String groupItemNames;

    @ApiModelProperty(value = "售价")
    @TableField(exist = false)
    private Integer price;

    @ApiModelProperty(value = "诊断标准")
    private String diagnosticCriteria;

    @ApiModelProperty(value = "症状询问")
    private String symptomInquiry;


    @ApiModelProperty(value = "原价")
    @TableField(exist = false)
    private Integer costPriceAll;
    /*以下参数为查询根据用户的体检报告id查询对应的体检项目*/
    @TableField(exist = false)
    private String itemName;
    @TableField(exist = false)
    private Integer salePrice;
    @TableField(exist = false)
    private String officeName;
    @TableField(exist = false)
    private String address;
    @TableField(exist = false)
    private String costPrice;
    @TableField(exist = false)
    private Integer SumPrice;

}
