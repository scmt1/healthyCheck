package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Date;

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
 * @author liubingcheng
 * @since 2023-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TDiseaseDiagnosis对象", description="")
public class TDiseaseDiagnosis implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员id")
    private String personId;

    @ApiModelProperty(value = "细菌性痢疾")
    private Integer isDiseaseOne;

    @ApiModelProperty(value = "伤寒和副伤寒")
    private Integer isDiseaseTwo;

    @ApiModelProperty(value = "病毒性肝炎（甲型、戊型）")
    private Integer isDiseaseThree;

    @ApiModelProperty(value = "活动性肺结核")
    private Integer isDiseaseFour;

    @ApiModelProperty(value = "化脓性或渗出性皮肤病")
    private Integer isDiseaseFive;

    @ApiModelProperty(value = "手癣、指甲癣")
    private Integer isDiseaseSix;

    @ApiModelProperty(value = "手部湿疹")
    private Integer isDiseaseSeven;

    @ApiModelProperty(value = "手部的银屑病或者鳞屑")
    private Integer isDiseaseEight;

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


}
