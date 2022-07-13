package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author ycy
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TOrderGroupItemProject对象", description="")
public class TOrderGroupItemProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "分组项目id")
    private String tOrderGroupItemId;

    @ApiModelProperty(value = "项目代码")
    private String code;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "基础项目id")
    private String baseProjectId;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "排序")
    private Float orderNum;

    @ApiModelProperty(value = "科室id")
    private String officeId;

    @ApiModelProperty(value = "科室名称")
    private String officeName;

    @ApiModelProperty(value = "项目单位")
    private String unitCode;
    @ApiModelProperty(value = "项目单位")
    private String unitName;
    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    @ApiModelProperty(value = "结果类型")
    private String resultType;

    @ApiModelProperty(value = "是否进入小结")
    private String inConclusion;

    @ApiModelProperty(value = "是否进入报告")
    private String inReport;

    @ApiModelProperty(value = "LIS关联码")
    private String relationCode;

    @ApiModelProperty(value = "订单id")
    private String groupOrderId;

    @ApiModelProperty(value = "基础项目检查结果")
    @TableField(exist = false)
    private TDepartItemResult departItemResults;

    @ApiModelProperty(value = "复查项目结果")
    @TableField(exist = false)
    private List<TReviewProject> tReviewProjects;

    @ApiModelProperty(value = "基础项目参考值")
    @TableField(exist = false)
    private RelationProjectReference relationProjectReference;

    @ApiModelProperty(value = "基础项目危险值")
    @TableField(exist = false)
    private List<RelationProjectCritical> criticals;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    private Integer delFlag;
}
