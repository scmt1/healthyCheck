package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author dengjie
 * @since 2023-02-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TPositiveResults对象", description="")
public class TPositiveResults implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键Id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "删除标识（0-未删除，1-删除）")
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

    @ApiModelProperty(value = "创建人名称")
    private String createdUserName;

    @ApiModelProperty(value = "是否重度")
    private String degree;

    @ApiModelProperty(value = "部门Id")
    @TableField("deptId")
    private String deptId;

    @ApiModelProperty(value = "数据指南")
    @TableField("dietaryGuidance")
    private String dietaryGuidance;

    @ApiModelProperty(value = "健康建议")
    @TableField("healthAdvice")
    private String healthAdvice;

    @ApiModelProperty(value = "健康知识")
    @TableField("healthKnowledge")
    private String healthKnowledge;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "简拼")
    @TableField("namePinyin")
    private String namePinyin;

    private String pk;

    @ApiModelProperty(value = "是否阳性")
    private String positive;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "总结")
    private String summary;

    @ApiModelProperty(value = "提示")
    private String tips;

    @ApiModelProperty(value = "职业建议")
    private String advise;

    @ApiModelProperty(value = "备注")
    private String remark;

    @TableField("sportsGuidance")
    private String sportsGuidance;

    @TableField(exist = false)
    private List<TPositiveResults> positiveData;



}
