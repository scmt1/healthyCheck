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
import java.util.Date;

/**
 * <p>
 * 健康证管理表
 * </p>
 *
 * @author lbc
 * @since 2021-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TCertificateManage对象", description="健康证管理表")
public class TCertificateManage implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "订单ID")
    private String orderId;

    @ApiModelProperty(value = "编号")
    private String code;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "体检结果")
    private String results;

    @ApiModelProperty(value = "发证日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateOfIssue;

    @ApiModelProperty(value = "有效期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date termOfValidity;

    @ApiModelProperty(value = "发证单位")
    private String unitOfIssue;

    @ApiModelProperty(value = "条形码")
    private Object codeImg;

    @ApiModelProperty(value = "头像")
    private Object headImg;

    @ApiModelProperty(value = "是否显示(0-不显示，1-显示)")
    @TableLogic
    private Integer delFlag;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    private Integer isShow;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty(value = "删除人")
    private String deleteId;

    @ApiModelProperty(value = "删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;

    @ApiModelProperty(value = "人员id")
    private String personId;

    @ApiModelProperty(value = "头像路径")
    @TableField(exist = false)
    private String headImgPath;

    @ApiModelProperty(value = "分组id")
    @TableField(exist = false)
    private String groupId;

    @ApiModelProperty(value = "套餐名称")
    @TableField(exist = false)
    private String comboName;

    @ApiModelProperty(value = "从业类别")
    @TableField(exist = false)
    private String certificateType;

    @ApiModelProperty(value = "打印状态")
    @TableField(exist = false)
    private Integer printState;

    @ApiModelProperty(value = "登记时间")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registDate;

    @ApiModelProperty(value = "身份证号")
    @TableField(exist = false)
    private String idCard;

    @ApiModelProperty(value = "是否上传")
    private Integer isUpload;

    @ApiModelProperty(value = "上传异常信息")
    private String exceptionMessage;

    @ApiModelProperty(value = "联系电话")
    @TableField(exist = false)
    private String mobile;

    @ApiModelProperty(value = "健康证编码")
    private String healthCcertificate;

    @ApiModelProperty(value = "登记号码")
    private String registrationNumber;

    @ApiModelProperty(value = "出生日期")
    @TableField(exist = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;

    @ApiModelProperty(value = "单位统一信用代码")
    @TableField(exist = false)
    private String uscc;

    @ApiModelProperty(value = "单位名称")
    @TableField(exist = false)
    private String unitName;

    @ApiModelProperty(value = "民族")
    @TableField(exist = false)
    private String nation;

    @ApiModelProperty(value = "体检人员上传id")
    private String basicPersonId;

    @ApiModelProperty(value = "体检人员结果上传id")
    private String physicalExaminationId;

    @ApiModelProperty(value = "健康证上传id")
    private String medicalCertificateId;

}
