package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author dengjie
 * @since 2023-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TPositiveResultsRule对象", description="")
public class TPositiveResultsRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "条件")
    private String condition;

    @ApiModelProperty(value = "适合性别")
    private String genderAppropriate;

    @ApiModelProperty(value = "阳性Id")
    @TableField("positiveId")
    private String positiveId;

    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "规则值")
    private String regularValue;

    @ApiModelProperty(value = "判断条件")
    private String judgmentCondition;

    @ApiModelProperty(value = "排除阳性结果")
    private String excludePositive;

    @ApiModelProperty(value = "项目名称汇总")
    @TableField(exist = false)
    private List<TPositiveRule> rulData;

    @ApiModelProperty(value = "项目名称")
    @TableField(exist = false)
    private String projectNames;

    @ApiModelProperty(value = "类型")
    @TableField(exist = false)
    private String types;

    @ApiModelProperty(value = "规则值")
    @TableField(exist = false)
    private String regularValues;

}
