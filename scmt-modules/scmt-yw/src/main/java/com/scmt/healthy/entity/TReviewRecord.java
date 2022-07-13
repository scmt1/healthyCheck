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

/**
 * <p>
 *
 * </p>
 *
 * @author ycy
 * @since 2021-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TInspectionRecord对象", description="")
public class TReviewRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "体检人员Id")
    private String personId;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "科室id")
    private String officeId;

    @ApiModelProperty(value = "复查项目id")
    private String checkProjectId;

    @ApiModelProperty(value = "复查项目名称")
    private String checkProjectName;

    @ApiModelProperty(value = "复查说明")
    private String reviewExplain;

    @ApiModelProperty(value = "操作医生")
    private String operateDoctor;

    @ApiModelProperty(value = "复查日期")
    private Date reviewTime;

    @ApiModelProperty(value = "删除状态")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人id")
    private String createId;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "更新人id")
    private String updateId;

    @ApiModelProperty(value = "更新")
    private Date updateTime;

    @ApiModelProperty(value = "是否通过审核")
    private Integer state;

    @ApiModelProperty(value = "团检单位id")
    private String groupUnitId;

    @ApiModelProperty(value = "团检单位名称")
    private String groupUnitName;

    @ApiModelProperty(value = "团检订单id")
    private String groupOrderId;

    @ApiModelProperty(value = "团检订单id")
    @TableField(exist = false)
    private TReviewProject reviewProject;
}
