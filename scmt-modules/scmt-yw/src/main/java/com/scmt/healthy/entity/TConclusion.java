package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * pacs影像数据
 * </p>
 *
 * @author ....
 * @since 2021-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="TConclusion对象", description="pacs影像数据")
public class TConclusion implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "体检结论")
    private String comments;

    @ApiModelProperty(value = "影响所见")
    private String seeing;

    @ApiModelProperty(value = "检查部位")
    private String viewpos;

    @ApiModelProperty(value = "类型  DR B超")
    private String type;

    @ApiModelProperty(value = "插入时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty(value = "体检人员的barcode")
    private String code;

    @ApiModelProperty(value = "人员姓名")
    private String personName;
}
