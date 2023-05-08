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
import java.util.List;

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
public class TReviewProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员id")
    private String personId;

    @ApiModelProperty(value = "人员姓名")
    private String personName;

    @ApiModelProperty(value = "复查体检编号")
    private String testNum;

    @ApiModelProperty(value = "组合项目id")
    private String portfolioProjectId;

    @ApiModelProperty(value = "组合项目名")
    private String portfolioProjectName;

    @ApiModelProperty(value = "组合项目名")
    private String groupId;

    @ApiModelProperty(value = "分组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "名称")
    private String name;

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

    @ApiModelProperty(value = "基础项目参考值")
    @TableField(exist = false)
    private RelationProjectReference relationProjectReference;

    @ApiModelProperty(value = "基础项目危险值")
    @TableField(exist = false)
    private List<RelationProjectCritical> criticals;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    @TableLogic
    private Integer delFlag;

    @ApiModelProperty(value = "创建人id")
    private String createId;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "分组项目id")
    @TableField(exist = false)
    private String tOrderGroupItemId;

    @ApiModelProperty(value = "是否登记")
    private Integer isPass;

    @ApiModelProperty(value = "关键字查询")
    @TableField(exist = false)
    private String keyword;

    @ApiModelProperty(value = "年龄")
    @TableField(exist = false)
    private Integer age;
    @ApiModelProperty(value = "性别")
    @TableField(exist = false)
    private String sex;
    @ApiModelProperty(value = "性别")
    @TableField(exist = false)
    private String orderId;


    @ApiModelProperty(value = "检查地址")
    private String address;

    @ApiModelProperty(value = "登记日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registDate;

    @ApiModelProperty(value = "销售价（元）")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "折扣")
    private Integer discount;

    @ApiModelProperty(value = "折扣价(元)")
    private BigDecimal discountPrice;

    @ApiModelProperty(value = "项目类型 1套餐项目 2非套餐项目")
    private Integer projectType;

    @ApiModelProperty(value = "是否为附件")
    private String isFile;

    @ApiModelProperty(value = "服务类型")
    private String serviceType;

    @ApiModelProperty(value = "附件地址")
    private String url;
    @ApiModelProperty(value = "体检类别")
    private String physicalType;

    @ApiModelProperty(value = "检查状态")
    @TableField(exist = false)
    private Integer status;

    @ApiModelProperty(value = "复查原因")
    private String reason;

    @ApiModelProperty(value = "科室id集合，角色可能绑定多个")
    @TableField(exist = false)
    private List<String> officeList;

    @ApiModelProperty(value = "标本")
    private String specimen;

    @ApiModelProperty(value = "弃检原因")
    @TableField(exist = false)
    private String abandonRenson;

    @ApiModelProperty(value = "单位名称")
    @TableField(exist = false)
    private String dept;

    @ApiModelProperty(value = "身份证号")
    @TableField(exist = false)
    private String idCard;

    @ApiModelProperty(value = "危害因素")
    private String hazardFactorCode;
}
