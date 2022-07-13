package com.scmt.healthy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author dengjie
 * @since 2021-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="RelationPersonProjectCheck对象", description="")
public class RelationPersonProjectCheck implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "人员id")
    private String personId;

    @ApiModelProperty(value = "科室id")
    private String officeId;

    @ApiModelProperty(value = "状态  1已登记  2弃检")
    private Integer state;

    @ApiModelProperty(value = "分组项目id")
    private String orderGroupItemId;

    @ApiModelProperty(value = "弃检原因")
    private String abandonRenson;
    @ApiModelProperty(value = "分组项目id")
    @TableField(exist = false)
    private List<Map<String,Object>> itemIds = new ArrayList<>();
}
