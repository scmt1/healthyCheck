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
 * @author lbc
 * @since 2022-5-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TInterrogation对象", description="")
public class TInterrogation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "体检人员Id")
    private String personId;

    @ApiModelProperty(value = "总工龄年数")
    private Integer workYear;

    @ApiModelProperty(value = "总工龄月数")
    private Integer workMonth;

    @ApiModelProperty(value = "接害工龄年数")
    private Integer exposureWorkYear;

    @ApiModelProperty(value = "接害工龄月数")
    private Integer exposureWorkMonth;

    @ApiModelProperty(value = "接害开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date exposureStartDate;

    @ApiModelProperty(value = "删除标识（0-未删除，1-删除）")
    @TableLogic(value = "0", delval = "1")
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

    @ApiModelProperty(value = "民族")
    private String nation;


    @ApiModelProperty(value = "检查次数")
    private Integer checkNum;

    @ApiModelProperty(value = "急慢性职业病史")
    private String diseaseName;

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

    @ApiModelProperty(value = "问诊科检查医生")
    private String wzCheckDoctor;

    @ApiModelProperty(value = "问诊科检查时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date wzCheckTime;

    @ApiModelProperty(value = "问诊科检查医生签名")
    private Object wzCheckAutograph;

    @ApiModelProperty(value = "婚姻史-结婚日期")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date marriageDate;

    @ApiModelProperty(value = "配偶接触放射线情况")
    private String spouseRadiationSituation;

    @ApiModelProperty(value = "配偶职业及健康状况")
    private String spouseHealthSituation;

    @ApiModelProperty(value = "孕次")
    private Integer pregnancyCount;

    @ApiModelProperty(value = "活产")
    private Integer liveBirth;

    @ApiModelProperty(value = "自然流产")
    private Integer abortionSmall;

    @ApiModelProperty(value = "多胎")
    private Integer multiparous;

    @ApiModelProperty(value = "异位妊娠")
    private Integer ectopicPregnancy;

    @ApiModelProperty(value = "现有男孩")
    private Integer boys;

    @ApiModelProperty(value = "男孩出生日期")
    private String boysBirth;

    @ApiModelProperty(value = "现有女孩")
    private Integer girls;

    @ApiModelProperty(value = "女孩出生日期")
    private String girlsBirth;

    @ApiModelProperty(value = "不孕不育原因")
    private String infertilityReason;

    @ApiModelProperty(value = "子女健康情况")
    private String childrensHealth;

    @ApiModelProperty(value = "戒烟年数")
    private Integer quitSomking;

    @ApiModelProperty(value = "职务/职称")
    private String job;

    @ApiModelProperty(value = "邮政编码")
    private String zipCode;

}
