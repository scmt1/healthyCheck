package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Lob;

/**
 * <p>
 *
 * </p>
 *
 * @author mike
 * @since 2021-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TDepartResult对象", description="")
public class TDepartResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "体检人员Id")
    private String personId;

    @ApiModelProperty(value = "分组id")
    private String groupItemId;

    @ApiModelProperty(value = "分组名称")
    private String groupItemName;

    @ApiModelProperty(value = "检查医生")
    private String checkDoc;

    @ApiModelProperty(value = "检查日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkDate;

    @ApiModelProperty(value = "删除状态")
    private Integer delFlag;

    @ApiModelProperty(value = "结果状态(0-一般，1-最优)")
    private Integer state;

    @ApiModelProperty(value = "创建人id")
    private String createId;

    @ApiModelProperty(value = "创建日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    @ApiModelProperty(value = "更新人id")
    private String updateId;

    @ApiModelProperty(value = "更新")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    @ApiModelProperty(value = "删除人id")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteDate;

    @ApiModelProperty(value = "科室id")
    private String officeId;

    @ApiModelProperty(value = "科室名称")
    private String officeName;

    @ApiModelProperty(value = "科室名称")
    @TableField(exist = false)
    private String sectionName;

    @ApiModelProperty(value = "诊断小结")
    private String diagnoseSum;

    @ApiModelProperty(value = "诊断提示")
    private String diagnoseTip;

    @ApiModelProperty(value = "检查次数")
    private Integer checkNum;

    @ApiModelProperty(value = "检查项目名")
    @TableField(exist = false)
    private String checkProName;

    @ApiModelProperty(value = "是否为附件")
    private String isFile;

    @ApiModelProperty(value = "附件地址")
    private String url;

    @ApiModelProperty(value = "是否复查")
    private Integer isRecheck;

    @ApiModelProperty(value = "签名")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name=" autograph", columnDefinition="longblob", nullable=true)
    private byte[] checkSign;

    @ApiModelProperty(value = "组合项目Id")
    @TableField(exist = false)
    private String portfolioProjectId;

    @ApiModelProperty(value = "分组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "项目排序")
    @TableField(exist = false)
    private String orderNumProject;

    @ApiModelProperty(value = "科室排序")
    @TableField(exist = false)
    private String orderNumOffice;

    @ApiModelProperty(value = "基础项结果")
    @TableField(exist = false)
    private List<TDepartItemResult> tDepartItemResults;
}
