package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    @TableField(exist = false)
    private Integer workYear;

    @ApiModelProperty(value = "总工龄月数")
    @TableField(exist = false)
    private Integer workMonth;

    @ApiModelProperty(value = "接害工龄年数")
    @TableField(exist = false)
    private Integer exposureWorkYear;

    @ApiModelProperty(value = "接害工龄月数")
    @TableField(exist = false)
    private Integer exposureWorkMonth;

    @ApiModelProperty(value = "工种其他名称")
    private String workName;

    @ApiModelProperty(value = "在岗状态编码")
    private String workStateCode;

    @ApiModelProperty(value = "在岗状态名称")
    private String workStateText;

    @ApiModelProperty(value = "接害开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(exist = false)
    private Date exposureStartDate;

    @ApiModelProperty(value = "工种代码")
    private String workTypeCode;

    @ApiModelProperty(value = "工种代码")
    private String workTypeText;

    @ApiModelProperty(value = "监测类型")
    private String jcType;

    @ApiModelProperty(value = "从业类别")
    private String certificateType;

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

    @ApiModelProperty(value = "头像地址")
    @TableField(exist = false)
    private String avatarSignPath;

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
    @TableField(exist = false)
    private Integer checkNum;

    @ApiModelProperty(value = "急慢性职业病史")
    @TableField(exist = false)
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
    @TableField(exist = false)
    private String isCured;
    @ApiModelProperty(value = "初潮")
    @TableField(exist = false)
    private Integer menarche;
    @ApiModelProperty(value = "经期")
    @TableField(exist = false)
    private Integer period;
    @ApiModelProperty(value = "周期")
    @TableField(exist = false)
    private Integer cycle;
    @ApiModelProperty(value = "末次月经")
    @TableField(exist = false)
    private String lastMenstruation;
    @ApiModelProperty(value = "现有子女")
    @TableField(exist = false)
    private Integer existingChildren;
    @ApiModelProperty(value = "流产")
    @TableField(exist = false)
    private Integer abortion;
    @ApiModelProperty(value = "早产")
    @TableField(exist = false)
    private Integer premature;
    @ApiModelProperty(value = "死亡")
    @TableField(exist = false)
    private Integer death;
    @ApiModelProperty(value = "异常胎")
    @TableField(exist = false)
    private Integer abnormalFetus;

    @ApiModelProperty(value = "吸烟史")
    @TableField(exist = false)
    private String smokeState;
    @ApiModelProperty(value = "包每天")
    @TableField(exist = false)
    private BigDecimal packageEveryDay;

    @ApiModelProperty(value = "吸烟年数")
    @TableField(exist = false)
    private Integer smokeYear;

    @ApiModelProperty(value = "戒烟年数")
    @TableField(exist = false)
    private Integer quitSomking;

    @ApiModelProperty(value = "职务/职称")
    @TableField(exist = false)
    private String job;

    @ApiModelProperty(value = "邮政编码")
    @TableField(exist = false)
    private String zipCode;

    @ApiModelProperty(value = "喝酒史")
    @TableField(exist = false)
    private String drinkState;
    @ApiModelProperty(value = "ml每天")
    @TableField(exist = false)
    private Integer mlEveryDay;
    @ApiModelProperty(value = "喝酒年数")
    @TableField(exist = false)
    private Integer drinkYear;
    @ApiModelProperty(value = "其他信息")
    @TableField(exist = false)
    private String otherInfo;
    @ApiModelProperty(value = "症状")
    @TableField(exist = false)
    private String symptom;

    @TableField(exist = false)
    @ApiModelProperty(value = "类型")
    private String type;

    @TableField(exist = false)
    @ApiModelProperty(value = "危害因素体检结论代码")
    private String BadrsnconclusionCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "弃检数目")
    private String qjCount;

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
    @TableField(exist = false)
    private String education;
    @ApiModelProperty(value = "家庭地址")
    @TableField(exist = false)
    private String familyAddress;
    @ApiModelProperty(value = "月经史")
    @TableField(exist = false)
    private String menstrualHistory;
    @ApiModelProperty(value = "月经史异常信息")
    @TableField(exist = false)
    private String menstrualInfo;
    @ApiModelProperty(value = "过敏史")
    @TableField(exist = false)
    private String allergies;
    @ApiModelProperty(value = "过敏史信息")
    @TableField(exist = false)
    private String allergiesInfo;
    @ApiModelProperty(value = "出生地code")
    @TableField(exist = false)
    private String birthplaceCode;
    @ApiModelProperty(value = "出生地名称")
    @TableField(exist = false)
    private String birthplaceName;
    @ApiModelProperty(value = "家族史")
    @TableField(exist = false)
    private String familyHistory;
    @ApiModelProperty(value = "既往病史其他信息")
    @TableField(exist = false)
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
    @TableField(exist = false)
    private String wzCheckDoctor;
    @ApiModelProperty(value = "问诊科检查医生")
    @TableField(exist = false)
    private String wzCheckDoctorId;
    @ApiModelProperty(value = "问诊科检查时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date wzCheckTime;

    @ApiModelProperty(value = "体检日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date checkDate;

    @ApiModelProperty(value = "问诊科检查医生签名")
    @TableField(exist = false)
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


    @ApiModelProperty(value = "分组")
    @TableField(exist = false)
    private TOrderGroup groupData;



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

    @ApiModelProperty(value = "复查结论编码")
    @TableField(exist = false)
    private String reviewResultCode;

    @ApiModelProperty(value = "复查建议")
    @TableField(exist = false)
    private String reviewOpinion;

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

    @ApiModelProperty(value = "婚姻史-结婚日期")
    @TableField(exist = false)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date marriageDate;

    @ApiModelProperty(value = "配偶接触放射线情况")
    @TableField(exist = false)
    private String spouseRadiationSituation;

    @ApiModelProperty(value = "配偶职业及健康状况")
    @TableField(exist = false)
    private String spouseHealthSituation;

    @ApiModelProperty(value = "孕次")
    @TableField(exist = false)
    private Integer pregnancyCount;

    @ApiModelProperty(value = "活产")
    @TableField(exist = false)
    private Integer liveBirth;

    @ApiModelProperty(value = "自然流产")
    @TableField(exist = false)
    private Integer abortionSmall;

    @ApiModelProperty(value = "多胎")
    @TableField(exist = false)
    private Integer multiparous;

    @ApiModelProperty(value = "异位妊娠")
    @TableField(exist = false)
    private Integer ectopicPregnancy;

    @ApiModelProperty(value = "现有男孩")
    @TableField(exist = false)
    private Integer boys;

    @ApiModelProperty(value = "男孩出生日期")
    @TableField(exist = false)
    private String boysBirth;

    @ApiModelProperty(value = "现有女孩")
    @TableField(exist = false)
    private Integer girls;

    @ApiModelProperty(value = "女孩出生日期")
    @TableField(exist = false)
    private String girlsBirth;

    @ApiModelProperty(value = "不孕不育原因")
    @TableField(exist = false)
    private String infertilityReason;

    @ApiModelProperty(value = "子女健康情况")
    @TableField(exist = false)
    private String childrensHealth;

    @ApiModelProperty(value = "职业照射种类")
    @TableField(exist = false)
    private String irradiationType;

    @ApiModelProperty(value = "职业照射种类代码")
    @TableField(exist = false)
    private String irradiationTypeCode;

    @ApiModelProperty(value = "上次体检的人员id")
    @TableField(exist = false)
    private String oldPersonId;

    @ApiModelProperty(value = "第一次体检的人员id")
    @TableField(exist = false)
    private String firstPersonId;

    @ApiModelProperty(value = "体检代码")
    @TableField(exist = false)
    private String conclusionCode;

    @ApiModelProperty(value = "体检结论")
    @TableField(exist = false)
    private String conclusionOld;

    @ApiModelProperty(value = "体检结论")
    @TableField(exist = false)
    private String title;

    @ApiModelProperty(value = "分组人数可以为零")
    @TableField(exist = false)
    private Boolean tolerable;

    @TableField(exist = false)
    private String groupUnitName;

    @ApiModelProperty(value = "疾病诊断")
    @TableField(exist = false)
    private TDiseaseDiagnosis tDiseaseDiagnosis;

    @ApiModelProperty(value = "团检单位信息")
    @TableField(exist = false)
    private TGroupUnit groupUnit;

    @ApiModelProperty("统一社会信用代码")
    @TableField(exist = false)
    private String uscc;

    @ApiModelProperty("登记号码")
    @TableField(exist = false)
    private String registrationNumber;

    @ApiModelProperty()
    @TableField(exist = false)
    private String  basicPersonId;

    @ApiModelProperty("职业禁忌证")
    @TableField(exist = false)
    private String occupationalTabooNow;

    @ApiModelProperty("疑是职业病")
    @TableField(exist = false)
    private String occupationalDiseasesNow;

    @ApiModelProperty("危害因素")
    @TableField(exist = false)
    private String[] hazardFactorCode;

    @ApiModelProperty("家庭住址")
    private String homeAddress;

    @ApiModelProperty("体检机构id")
    @TableField(exist = false)
    private String checkOrgId;
}
