package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author mike
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TReviewPerson对象", description="")
public class TReviewPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员姓名")
    private String personName;

    @ApiModelProperty(value = "证件号码")
    private String idCard;

    @ApiModelProperty(value = "性别")
    @TableField(exist = false)
    private String sex;

    @ApiModelProperty(value = "年龄")
    @TableField(exist = false)
    private String age;
    @ApiModelProperty(value = "电话")
    @TableField(exist = false)
    private String mobile;

    @ApiModelProperty(value = "在岗状态")
    @TableField(exist = false)
    private String workStateText;
    @ApiModelProperty(value = "在岗状态编码")
    @TableField(exist = false)
    private String workStateCode;

    @ApiModelProperty(value = "体检结论")
    @TableField(exist = false)
    private String title;

    @ApiModelProperty(value = "未完成项目")
    @TableField(exist = false)
    private String noCheckProjectName;

    @ApiModelProperty(value = "危害因素")
    @TableField(exist = false)
    private String hazardFactorsText;

    @ApiModelProperty(value = "体检人员工作部门(单位名称)")
    private String dept;

    @ApiModelProperty(value = "删除标识（0-未删除，1-删除）")
    @TableLogic
    private Integer delFlag;

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

    @ApiModelProperty(value = "订单id")
    private String orderId;

    @ApiModelProperty(value = "分组id")
    private String groupId;

    @ApiModelProperty(value = "是否通过检查")
    private Integer isPass;

    @ApiModelProperty(value = "体检编号")
    private String testNum;

    @ApiModelProperty(value = "体检类别")
    private String physicalType;

    @ApiModelProperty(value = "单位id")
    private String unitId;

    @ApiModelProperty(value = "诊断日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date diagnosisDate;

    @ApiModelProperty(value = "登记日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registDate;

    @ApiModelProperty(value = "上次体检的人员id")
    private String oldPersonId;

    @ApiModelProperty(value = "体检结果（0-未见异常，1-其他异常，2-职业禁忌症，3-疑似职业病）")
    private String checkResult;

    @ApiModelProperty(value = "是否复查（0-不复查，1-复查）")
    private Integer isRecheck;

    @ApiModelProperty(value = "上次体检的人员id")
    private String firstPersonId;

    @ApiModelProperty(value = "打印状态")
    private Integer printState;

    @ApiModelProperty(value = "科室名字")
    @TableField(exist = false)
    private String officeName;

    @ApiModelProperty(value = "组合项目Id")
    @TableField(exist = false)
    private String portfolioProjectId;

    @ApiModelProperty(value = "复查项目名称")
    @TableField(exist = false)
    private String portfolioProjectName;

    @ApiModelProperty(value = "复查项目价格")
    @TableField(exist = false)
    private String portfolioProjectPrice;

    @ApiModelProperty(value = "销售价（元）")
    @TableField(exist = false)
    private BigDecimal salePrice;

    @ApiModelProperty(value = "危害因素编码")
    String hazardFactorCode;

}
