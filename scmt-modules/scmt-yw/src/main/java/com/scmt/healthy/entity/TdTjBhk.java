package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 体检主表信息
 * </p>
 *
 * @author dengjie
 * @since 2021-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TdTjBhk对象", description="体检主表信息")
public class TdTjBhk implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "业务系统主键")
    @TableField("RID")
    private String rid;

    @ApiModelProperty(value = "体检机构编号，由职业卫生平台提供的机构编码")
    @TableField("BHKORGAN_CODE")
    private String bhkorganCode;

    @ApiModelProperty(value = "体检编号，机构内需唯一")
    @TableField("BHK_CODE")
    private String bhkCode;

    @ApiModelProperty(value = "社会信用代码")
    @TableField("INSTITUTION_CODE")
    private String institutionCode;

    @ApiModelProperty(value = "企业名称")
    @TableField("CRPT_NAME")
    private String crptName;

    @ApiModelProperty(value = "企业注册地址")
    @TableField("CRPT_ADDR")
    private String crptAddr;

    @ApiModelProperty(value = "人员姓名")
    @TableField("PERSON_NAME")
    private String personName;

    @ApiModelProperty(value = "性别")
    @TableField("SEX")
    private Integer sex;

    @ApiModelProperty(value = "证件号码")
    @TableField("IDC")
    private String idc;

    @ApiModelProperty(value = "出生日期")
    @TableField("BRTH")
    private String brth;

    @ApiModelProperty(value = "年龄")
    @TableField("AGE")
    private Integer age;

    @ApiModelProperty(value = "婚否")
    @TableField("ISXMRD")
    private Integer isxmrd;

    @ApiModelProperty(value = "人员联系电话")
    @TableField("LNKTEL")
    private String lnktel;

    @ApiModelProperty(value = "体检人员工作部门")
    @TableField("DPT")
    private String dpt;

    @ApiModelProperty(value = "人员工号")
    @TableField("WRKNUM")
    private String wrknum;

    @ApiModelProperty(value = "总工龄年数")
    @TableField("WRKLNT")
    private Integer wrklnt;

    @ApiModelProperty(value = "总工龄月数,不能超过12,当总工龄年数不为空时，该字段必填")
    @TableField("WRKLNTMONTH")
    private Integer wrklntmonth;

    @ApiModelProperty(value = "接害工龄年数,不能超过12")
    @TableField("TCHBADRSNTIM")
    private Integer tchbadrsntim;

    @ApiModelProperty(value = "接害工龄月数")
    @TableField("TCHBADRSNMONTH")
    private Integer tchbadrsnmonth;

    @ApiModelProperty(value = "工种其他名称")
    @TableField("WORK_NAME")
    private String workName;

    @ApiModelProperty(value = "在岗状态编码")
    @TableField("ONGUARD_STATE")
    private String onguardState;

    @ApiModelProperty(value = "体检日期")
    @TableField("BHK_DATE")
    private String bhkDate;

    @ApiModelProperty(value = "体检结果")
    @TableField("BHKRST")
    private String bhkrst;

    @ApiModelProperty(value = "主检建议")
    @TableField("MHKADV")
    private String mhkadv;

    @ApiModelProperty(value = "体检结论")
    @TableField("VERDICT")
    private String verdict;

    @ApiModelProperty(value = "主检医师")
    @TableField("MHKDCT")
    private String mhkdct;

    @ApiModelProperty(value = "体检类型编码")
    @TableField("BHK_TYPE")
    private Integer bhkType;

    @ApiModelProperty(value = "主检判定日期")
    @TableField("JDGDAT")
    private String jdgdat;

    @ApiModelProperty(value = "接害因素")
    @TableField("BADRSN")
    private String badrsn;

    @ApiModelProperty(value = "是否为复检")
    @TableField("IF_RHK")
    private Integer ifRhk;

    @ApiModelProperty(value = "复检对应的上次体检编号")
    @TableField("LAST_BHK_CODE")
    private String lastBhkCode;

    @ApiModelProperty(value = "身份证件类型代码")
    @TableField("ID_CARD_TYPE_CODE")
    private String idCardTypeCode;

    @ApiModelProperty(value = "工种代码")
    @TableField("WORK_TYPE_CODE")
    private String workTypeCode;

    @ApiModelProperty(value = "开始接害日期")
    @TableField("HARM_START_DATE")
    private String harmStartDate;

    @ApiModelProperty(value = "监测类型")
    @TableField("JC_TYPE")
    private Integer jcType;

    @ApiModelProperty(value = "报告打印日期")
    @TableField("RPT_PRINT_DATE")
    private String rptPrintDate;

    @ApiModelProperty(value = "用工单位社会信用代码")
    @TableField("CREDIT_CODE_EMP")
    private String creditCodeEmp;

    @ApiModelProperty(value = "用工单位名称")
    @TableField("CRPT_NAME_EMP")
    private String crptNameEmp;

    @ApiModelProperty(value = "用工单位行业类别编码")
    @TableField("INDUS_TYPE_CODE_EMP")
    private String indusTypeCodeEmp;

    @ApiModelProperty(value = "用工单位经济类型编码")
    @TableField("ECONOMY_CODE_EMP")
    private String economyCodeEmp;

    @ApiModelProperty(value = "用工单位企业规模编码")
    @TableField("CRPT_SIZE_CODE_EMP")
    private String crptSizeCodeEmp;

    @ApiModelProperty(value = "用工单位所属地区编码")
    @TableField("ZONE_CODE_EMP")
    private String zoneCodeEmp;

    @ApiModelProperty(value = "是否上传标志 是否上传标识--0：未上传 1：上传成功 2：上传失败")
    private Integer flag;

    @ApiModelProperty(value = "错误日志")
    private String errorInfo;
}
