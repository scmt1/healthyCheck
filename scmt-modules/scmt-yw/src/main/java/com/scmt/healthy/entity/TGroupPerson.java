package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

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
@ApiModel(value="TGroupPerson对象", description="")
public class TGroupPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员姓名")
    private String personName;


    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "证件号码")
    private String idCard;

    @ApiModelProperty(value = "出生日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;

    @ApiModelProperty(value = "年龄")
    private Integer age;

    @ApiModelProperty(value = "是否结婚")
    private String isMarry;

    @ApiModelProperty(value = "所属部门")
    private String department;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "体检人员工作部门(单位名称)")
    private String dept;

    @ApiModelProperty(value = "人员工号")
    private String workNum;

    @ApiModelProperty(value = "总工龄年数")
    private Integer workYear;

    @ApiModelProperty(value = "总工龄月数")
    private Integer workMonth;

    @ApiModelProperty(value = "接害工龄年数")
    private Integer exposureWorkYear;

    @ApiModelProperty(value = "接害工龄月数")
    private Integer exposureWorkMonth;

    @ApiModelProperty(value = "工种其他名称")
    private String workName;

    @ApiModelProperty(value = "在岗状态编码")
    private String workStateCode;

    @ApiModelProperty(value = "在岗状态名称")
    private String workStateText;

    @ApiModelProperty(value = "接害开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date exposureStartDate;

    @ApiModelProperty(value = "工种代码")
    private String workTypeCode;

    @ApiModelProperty(value = "工种代码")
    private String workTypeText;

    @ApiModelProperty(value = "监测类型")
    private String jcType;

    @ApiModelProperty(value = "删除标识（0-未删除，1-删除）")
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
    @ApiModelProperty(value = "分组名称")
    @TableField(exist = false)
    private String groupName;

    @ApiModelProperty(value = "是否通过检查")
    private Integer isPass;

    @ApiModelProperty(value = "体检编号")
    private String testNum;

    @ApiModelProperty(value = "头像地址")
    private Object avatar;

    @ApiModelProperty(value = "体检类别")
    private String physicalType;

    @ApiModelProperty(value = "民族")
    private String nation;

    @ApiModelProperty(value = "单位地址")
    private String addressUnit;

    @ApiModelProperty(value = "单位名称")
    @TableField(exist = false)
    private String unitName;

    @ApiModelProperty(value = "单位id")
    private String unitId;

    @ApiModelProperty(value = "关键字查询")
    @TableField(exist = false)
    private String keyword;

    @ApiModelProperty(value = "状态")
    private Integer statu;

    @ApiModelProperty(value = "危害因素")
    private String hazardFactors;

    @ApiModelProperty(value = "危害因素")
    private String hazardFactorsText;

    @ApiModelProperty(value = "其他危害因素")
    private String otherHazardFactors;

    @ApiModelProperty(value = "其他是否检查")
    private Integer isCheck;

    @ApiModelProperty(value = "问诊是否检查")
    private Integer isWzCheck;


    @ApiModelProperty(value = "检查次数")
    private Integer checkNum;

    @ApiModelProperty(value = "急慢性职业病史")
    private String diseaseName;
    @ApiModelProperty(value = "诊断日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date diagnosisDate;

    @ApiModelProperty(value = "登记日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date registDate;

    @ApiModelProperty(value = "诊断单位")
    private String diagnosticUnit;
    @ApiModelProperty(value = "是否痊愈")
    private String isCured;
    @ApiModelProperty(value = "初潮")
    private Integer menarche;
    @ApiModelProperty(value = "经期")
    private Integer period;
    @ApiModelProperty(value = "周期")
    private Integer cycle;
    @ApiModelProperty(value = "末次月经")
    private String lastMenstruation;
    @ApiModelProperty(value = "现有子女")
    private Integer existingChildren;
    @ApiModelProperty(value = "流产")
    private Integer abortion;
    @ApiModelProperty(value = "早产")
    private Integer premature;
    @ApiModelProperty(value = "死亡")
    private Integer death;
    @ApiModelProperty(value = "异常胎")
    private Integer abnormalFetus;

    @ApiModelProperty(value = "吸烟史")
    private String smokeState;
    @ApiModelProperty(value = "包每天")
    private BigDecimal packageEveryDay;

    @ApiModelProperty(value = "吸烟年数")
    private Integer smokeYear;

    @ApiModelProperty(value = "喝酒史")
    private String drinkState;
    @ApiModelProperty(value = "ml每天")
    private Integer mlEveryDay;
    @ApiModelProperty(value = "喝酒年数")
    private Integer drinkYear;
    @ApiModelProperty(value = "其他信息")
    private String otherInfo;
    @ApiModelProperty(value = "症状")
    private String symptom;

    @TableField(exist = false)
    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "职业史")
    @TableField(exist = false)
    private List<TCareerHistory> careerHistoryData = new ArrayList<>();

    @ApiModelProperty(value = "未检查项目")
    @TableField(exist = false)
    private String noCheckProjectName;

    @ApiModelProperty(value = "检查完成人员数量")
    @TableField(exist = false)
    private Integer physicalFinishNum;

    @ApiModelProperty(value = "总检完成人员数量")
    @TableField(exist = false)
    private Integer inspectionFinishNum;

    @ApiModelProperty(value = "检查人员数量")
    @TableField(exist = false)
    private Integer allNum;

    @ApiModelProperty(value = "文化程度")
    private String education;
    @ApiModelProperty(value = "家庭地址")
    private String familyAddress;
    @ApiModelProperty(value = "月经史")
    private String menstrualHistory;
    @ApiModelProperty(value = "月经史异常信息")
    private String menstrualInfo;
    @ApiModelProperty(value = "过敏史")
    private String allergies;
    @ApiModelProperty(value = "过敏史信息")
    private String allergiesInfo;
    @ApiModelProperty(value = "出生地code")
    private String birthplaceCode;
    @ApiModelProperty(value = "出生地名称")
    private String birthplaceName;
    @ApiModelProperty(value = "家族史")
    private String familyHistory;
    @ApiModelProperty(value = "既往病史其他信息")
    private String pastMedicalHistoryOtherInfo;
    @ApiModelProperty(value = "报告打印次数")
    private Integer reportPrintingNum;
    @ApiModelProperty(value = "是否复查")
    private Integer isRecheck;


    @ApiModelProperty(value = "既往病史")
    @TableField(exist = false)
    private List<TPastMedicalHistory> pastMedicalHistoryData = new ArrayList<>();

    @ApiModelProperty(value = "症状")
    @TableField(exist = false)
    private List<TSymptom> symptomData = new ArrayList<>();

    @ApiModelProperty(value = "订单id数组")
    @TableField(exist = false)
    private List<String> orderIdList;

    @ApiModelProperty(value = "id集合")
    @TableField(exist = false)
    private String[] ids;

    @ApiModelProperty(value = "总检信息")
    @TableField(exist = false)
    private TInspectionRecord tInspectionRecord;

    @ApiModelProperty(value = "健康证条件")
    @TableField(exist = false)
    private String healthCertificateConditions;


    @ApiModelProperty(value = "目标职业病")
    @TableField(exist = false)
    private String occupationalDiseases;

    @ApiModelProperty(value = "目标职业禁忌症")
    @TableField(exist = false)
    private String occupationalTaboo;

    @ApiModelProperty(value = "问诊科检查医生")
    private String wzCheckDoctor;
    @ApiModelProperty(value = "问诊科检查时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date wzCheckTime;

    @ApiModelProperty(value = "体检日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date checkDate;

    @ApiModelProperty(value = "问诊科检查医生签名")
    private Object wzCheckAutograph;

    @ApiModelProperty(value = "基础项目id")
    @TableField(exist = false)
    private String portfolioId;

    @ApiModelProperty(value = "基础项目名称")
    @TableField(exist = false)
    private String portfolioName;

    @ApiModelProperty(value = "检查地址")
    @TableField(exist = false)
    private String address;

    @ApiModelProperty(value = "体检人员数量")
    @TableField(exist = false)
    private Integer personNum;

    @ApiModelProperty(value = "分组体检人员数量")
    @TableField(exist = false)
    private Integer groupPersonNum;

    @ApiModelProperty(value = "体检结果（0-未见异常，1-其他异常，2-职业禁忌症，3-疑似职业病）")
    private String checkResult;

    @ApiModelProperty(value = "分组项目")
    @TableField(exist = false)
    private List<TOrderGroupItem> projectData;



    @ApiModelProperty(value = "是否合格")
    @TableField(exist = false)
    private String isQualified;


    @ApiModelProperty(value = "原分组id")
    private String oldGroupId;

    @ApiModelProperty(value = "是否有未检或异常（0-没有，1-有）")
    @TableField(exist = false)
    private Integer isAllChecked;

    @ApiModelProperty(value = "环评因素")
    @TableField(exist = false)
    private String eiaFactors;


    @ApiModelProperty(value = "相关异常结果")
    @TableField(exist = false)
    private String diagnoseTip;

    @ApiModelProperty(value = "复查项目")
    @TableField(exist = false)
    private String portfolioProjectName;

    @ApiModelProperty(value = "体检结果")
    @TableField(exist = false)
    private String conclusion;

    @ApiModelProperty(value = "处理意见")
    @TableField(exist = false)
    private String handleOpinion;

    @ApiModelProperty(value = "套餐名称 从业体检")
    @TableField(exist = false)
    private String comboName;

    @ApiModelProperty(value = "套餐Id 从业体检")
    @TableField(exist = false)
    private String comboId;

    @ApiModelProperty(value = "原因")
    @TableField(exist = false)
    private String reason;

    @ApiModelProperty(value = "体检类型")
    @TableField(exist = false)
    private String workStateName;

    @ApiModelProperty(value = "危害因素")
    @TableField(exist = false)
    private String hazardFactorsName;

    @ApiModelProperty(value = "其他异常描述")
    @TableField(exist = false)
    private String otherCheckAbnormalResults;

    @ApiModelProperty(value = "其他异常处理意见")
    @TableField(exist = false)
    private String careerIllnessName;

    @ApiModelProperty(value = "复查结论")
    @TableField(exist = false)
    private String reviewResult;

    @ApiModelProperty(value = "复查项目名称")
    @TableField(exist = false)
    private String reviewName;

    @ApiModelProperty(value = "是否零星体检（0-没有，1-有）")
    private Integer sporadicPhysical;

    @ApiModelProperty(value = "打印状态")
    private Integer printState;

    @ApiModelProperty(value = "复查状态(0-未出结论，1-已出结论)")
    private Integer reviewStatu;

    @ApiModelProperty(value = "组合项目名称集")
    @TableField(exist = false)
    private String groupItemNames;

    @ApiModelProperty(value = "人员姓名")
    @TableField(exist = false)
    private String username;

    @ApiModelProperty(value = "app端分组项目")
    @TableField(exist = false)
    private Set<TOrderGroupItem> projectData2;

    @ApiModelProperty(value = "是否复查")
    @TableField(exist = false)
    private Boolean isReviewer;
}
