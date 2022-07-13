package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author Mike
 * @since 2021-09-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RelationProjectRules对象", description="")
public class RelationProjectRules implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "简称")
    private String shortName;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "区间值")
    private String intervalValue;

    @ApiModelProperty(value = "适合性别")
    private String allowSex;

    @ApiModelProperty(value = "年龄最低值")
    private Integer minAge;

    @ApiModelProperty(value = "年龄最高值")
    private Integer maxAge;

    @ApiModelProperty(value = "是否进入小结")
    private String inConclusion;

    @ApiModelProperty(value = "是否阳性")
    private String positive;

    @ApiModelProperty(value = "职业建议")
    private String careerAdvice;

    @ApiModelProperty(value = "健康建议")
    private String healthAdvice;

    @ApiModelProperty(value = "饮食指导")
    private String dietaryGuidance;

    @ApiModelProperty(value = "运动指导")
    private String sportsGuidance;

    @ApiModelProperty(value = "健康知识")
    private String healthKnowledge;

    @ApiModelProperty(value = "温馨提示")
    private String reminder;

    @ApiModelProperty(value = "所属部门")
    private String departmentId;

    @ApiModelProperty(value = "项目id")
    private String baseProjectId;


    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
