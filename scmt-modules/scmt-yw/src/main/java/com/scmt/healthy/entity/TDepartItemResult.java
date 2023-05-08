package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author mike
 * @since 2021-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TDepartItemResult对象", description="")
public class TDepartItemResult implements Serializable {

  private static final long serialVersionUID = 1L;

  @ApiModelProperty(value = "主键id")
  @TableId(value = "id", type = IdType.UUID)
  private String id;

  @ApiModelProperty(value = "选中基础体检项目id")
  private String orderGroupItemProjectId;

  @ApiModelProperty(value = "组合项目")
  private String orderGroupItemId;

  @ApiModelProperty(value = "组合项目名称")
  private String orderGroupItemName;

  @ApiModelProperty(value = "体检结果")
  @TableField(updateStrategy = FieldStrategy.IGNORED)
  private String result;

  @ApiModelProperty(value = "计量单位")
  private String unitCode;
  @ApiModelProperty(value = "计量单位")
  private String unitName;

  @ApiModelProperty(value = "范围")
  private String scope;

  @ApiModelProperty(value = "检查医生")
  private String checkDoc;

  @ApiModelProperty(value = "检查日期")
  private Date checkDate;

  @ApiModelProperty(value = "危急程度")
  private String crisisDegree;

  @ApiModelProperty(value = "阳性")
  private String positive;

  @ApiModelProperty(value = "删除状态")
//  @TableLogic
  private Integer delFlag;

  @ApiModelProperty(value = "创建人id")
  private String createId;

  @ApiModelProperty(value = "创建日期")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createDate;

  @ApiModelProperty(value = "更新人id")
  private String updateId;

  @ApiModelProperty(value = "更新时间")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateDate;

  @ApiModelProperty(value = "删除人id")
  private String deleteId;

  @ApiModelProperty(value = "删除时间")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date deleteDate;

  @ApiModelProperty(value = "指标箭头")
  private String arrow;

  @ApiModelProperty(value = "人员id")
  private String personId;

  @ApiModelProperty(value = "基础项目名称")
  private String orderGroupItemProjectName;

  @ApiModelProperty(value = "科室id")
  private String officeId;

  @ApiModelProperty(value = "科室名称")
  private String officeName;

  @ApiModelProperty(value = "影像图片，多个以逗号分隔")
  private String imgUrl;

  @ApiModelProperty(value = "人员姓名")
  @TableField(exist = false)
  private String personName;

  @ApiModelProperty(value = "体检编号")
  @TableField(exist = false)
  private String testNum;

  @ApiModelProperty(value = "手机号码")
  @TableField(exist = false)
  private String mobile;

  @ApiModelProperty(value = "是否通过检查")
  @TableField(exist = false)
  private Integer isPass;

  @ApiModelProperty(value = "是否忽略异常")
  private Integer ignoreStatus;

  @ApiModelProperty(value = "基础项目检查结果id")
  private String departResultId;

  @ApiModelProperty(value = "是否复查")
  private Integer isRecheck;

  @ApiModelProperty(value = "组合项目Id")
  @TableField(exist = false)
  private String groupItemId;

  @ApiModelProperty(value = "简称")
  @TableField(exist = false)
  private String shortName;

  @ApiModelProperty(value = "排序")
  private Float orderNum;

  @ApiModelProperty(value = "诊断小结")
  private String diagnoseSum;
}