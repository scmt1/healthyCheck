package com.scmt.base.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.scmt.core.base.ScmtBaseEntity;
import com.scmt.core.common.constant.CommonConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author Exrick
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_dict_data")
@TableName("t_dict_data")
@ApiModel(value = "字典数据")
public class DictData extends ScmtBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据名称")
    private String title;

    @ApiModelProperty(value = "数据值")
    private String value;

    @ApiModelProperty(value = "排序值")
    @Column(precision = 10, scale = 2)
    private BigDecimal sortOrder;

    @ApiModelProperty(value = "是否启用 0启用 -1禁用")
    private Integer status = CommonConstant.STATUS_NORMAL;

    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "所属字典")
    private String dictId;
}
