package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author dengjie
 * @since 2023-02-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TPositivePerson对象", description="")
public class TPositivePerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员id")
    private String personId;

    @ApiModelProperty(value = "创建人")
    private String createId;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "修改人")
    private String updateId;

    @ApiModelProperty(value = "阳性名称")
    private String positiveName;

    @ApiModelProperty(value = "阳性结果建议")
    private String positiveSuggestion;

    @ApiModelProperty(value = "是否重度")
    private String heavy;

    @ApiModelProperty(value = "结论类型")
    private String conclusionType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(exist = false)
    private List<TPositiveResults> positivePersonData;

    @ApiModelProperty(value = "排序zi")
    private Integer orderNum;


}
