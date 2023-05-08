package com.scmt.healthy.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 分组项目
 * </p>
 *
 * @author ycy
 * @since 2021-10-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TOrderGroupItem对象", description="分组项目")
public class TOrderGroupItem implements Serializable {

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

    @ApiModelProperty(value = "销售价（元）")
    private BigDecimal salePrice;

    @ApiModelProperty(value = "折扣")
    private Integer discount;

    @ApiModelProperty(value = "折扣价(元)")
    private BigDecimal discountPrice;

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

    @ApiModelProperty(value = "诊台是否显示")
    private String diagnostic;

    @ApiModelProperty(value = "分组id")
    private String groupId;
    @TableField(exist = false)
    @ApiModelProperty(value = "分组名称")
    private String groupName;

    @ApiModelProperty(value = "科室id")
    private String officeId;

    @ApiModelProperty(value = "科室名称")
    private String officeName;

    @ApiModelProperty(value = "组合项目id")
    private String portfolioProjectId;

    @ApiModelProperty(value = "项目类型 1套餐项目 2非套餐项目")
    private Integer projectType;

    @ApiModelProperty(value = "订单id")
    private String groupOrderId;

    @TableField(exist = false)
    @ApiModelProperty(value = "组合项目id")
    private List<TBaseProject> baseProjects;

    @TableField(exist = false)
    @ApiModelProperty(value = "诊断小结")
    private TDepartResult departResult;

    @ApiModelProperty(value = "是否为附件")
    private String isFile;

    @ApiModelProperty(value = "附件地址")
    private String url;

    @ApiModelProperty(value = "检查部位名称")
    private String deptName;

    @ApiModelProperty(value = "体检编号")
    @TableField(exist = false)
    private String testNum;

    @ApiModelProperty(value = "检查状态")
    @TableField(exist = false)
    private Integer status;

    @ApiModelProperty(value = "科室id集合，角色可能绑定多个")
    @TableField(exist = false)
    private List<String> officeList;

    @ApiModelProperty(value = "人员id")
    @TableField(exist = false)
    private String personId;

    @ApiModelProperty(value = "项目状态")
    @TableField(exist = false)
    private Integer itemStatus;

    @ApiModelProperty(value = "弃检原因")
    @TableField(exist = false)
    private String abandonRenson;
}
