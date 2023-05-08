package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.sql.Blob;
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
 *
 * </p>
 *
 * @author ycy
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TGroupUnit对象", description="")
public class TGroupUnit implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "单位名称")
    private String name;

    @ApiModelProperty(value = "统一社和信用代码")
    private String uscc;

    @ApiModelProperty(value = "上级单位社会信用代码")
    private String upperInstituttonCode;

    @ApiModelProperty(value = "用工单位是否分支机构 1是0否")
    private String ifSubOrg;

    @ApiModelProperty(value = "法人姓名")
    private String legalPerson;

    @ApiModelProperty(value = "注册资金")
    private String regCapital;

    @ApiModelProperty(value = "行业类型编码")
    private String industryCode;

    @ApiModelProperty(value = "行业类型名称")
    private String industryName;

    @ApiModelProperty(value = "企业规模编码")
    private String businessScaleCode;

    @ApiModelProperty(value = "企业规模名称")
    private String businessScaleName;

    @ApiModelProperty(value = "经济类型编码")
    private String economicTypeCode;

    @ApiModelProperty(value = "经济类型名称")
    private String economicTypeName;

    @ApiModelProperty(value = "单位注册地址")
    private String address;

    @ApiModelProperty(value = "所属部门")
    private String departmentId;

    @ApiModelProperty(value = "附件信息")
    private Object attachment;

    @ApiModelProperty(value = "是否删除(0-未删除，1-已删除)")
    @TableLogic(value = "0", delval = "1")
    private Integer delFlag;

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

    @ApiModelProperty(value = "所属地区编码")
    private String regionCode;

    @ApiModelProperty(value = "所属地区地址")
    private String regionName;

    @ApiModelProperty(value = "职工人数")
    private Integer employeesNum;

    @ApiModelProperty(value = "接触职业病危害因素人数")
    private Integer dangerNum;

    @ApiModelProperty(value = "法人电话")
    private String legalPhone;

    @ApiModelProperty(value = "生产工人数")
    private Integer workmanNum;

    @ApiModelProperty(value = "接触职业病危害因素女工人数")
    private Integer workmistressNum;

    @ApiModelProperty(value = "单位注册邮编")
    private String postalCode;

    @ApiModelProperty(value = "经营面积")
    private String workArea;

    @ApiModelProperty(value = "建档日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date filingDate;

    @ApiModelProperty(value = "建厂日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date establishmentDate;

    @ApiModelProperty(value = "检测联系人")
    private String linkMan1;

    @ApiModelProperty(value = "检测联系人职务")
    private String position1;

    @ApiModelProperty(value = "检测联系电话")
    private String linkPhone1;

    @ApiModelProperty(value = "体检联系人")
    private String linkMan2;

    @ApiModelProperty(value = "体检联系人职务")
    private String position2;

    @ApiModelProperty(value = "体检联系人电话")
    private String linkPhone2;

    @ApiModelProperty(value = "职业卫生安全负责人")
    private String safetyPrincipal;

    @ApiModelProperty(value = "安全联系人职务")
    private String safePosition;

    @ApiModelProperty(value = "安全联系人电话")
    private String safePhone;

    @ApiModelProperty(value = "隶属关系")
    private String subjeConn;

    @ApiModelProperty(value = "作业场所地址")
    private String enrolAddress;

    @ApiModelProperty(value = "作业场所邮编")
    private String enrolPostalCode;

    @ApiModelProperty(value = "职业卫生管理机构")
    private String occManaOffice;

    @ApiModelProperty(value = "体检类型")
    private String physicalType;

    @ApiModelProperty(value = "统一社和信用代码(用工单位)")
    private String creditCodeEmp;

    @ApiModelProperty(value = "单位名称(用工单位)")
    private String crptNameEmp;

    @ApiModelProperty(value = "行业类型编码(用工单位)")
    private String indusTypeCodeEmp;

    @ApiModelProperty(value = "经济类型编码(用工单位)")
    private String economyCodeEmp;

    @ApiModelProperty(value = "企业规模编码(用工单位)")
    private String crptSizeCodeEmp;

    @ApiModelProperty(value = "所属地区编码(用工单位)")
    private String zoneCodeEmp;

    @ApiModelProperty(value = "检测信息")
    @TableField(exist = false)
    private List<TTestRecord> testRecordData;


}
