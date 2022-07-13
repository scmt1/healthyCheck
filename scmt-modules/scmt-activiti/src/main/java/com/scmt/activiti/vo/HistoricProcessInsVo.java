package com.scmt.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.engine.history.HistoricProcessInstance;

import java.util.Date;

/**
 * @author Exrickx
 */
@Data
@Accessors(chain = true)
public class HistoricProcessInsVo {

    private String id;

    private String name;

    private String key;

    private Integer version;

    private String description;

    private String businessKey;

    private String tableId;

    private String procDefId;

    private String tenantId;

    private String deployId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Long duration;

    private String routeName;

    private String applyer;

    private String applyerUsername;

    private Integer result;

    private String deleteReason;

    public HistoricProcessInsVo(HistoricProcessInstance pi) {
        this.id = pi.getId();
        this.name = pi.getName();
        this.key = pi.getProcessDefinitionKey();
        this.version = pi.getProcessDefinitionVersion();
        this.description = pi.getDescription();
        this.businessKey = pi.getBusinessKey();
        this.procDefId = pi.getProcessDefinitionId();
        this.tenantId = pi.getTenantId();
        this.deployId = pi.getDeploymentId();
        this.startTime = pi.getStartTime();
        this.endTime = pi.getEndTime();
        this.duration = pi.getDurationInMillis();
        this.deleteReason = pi.getDeleteReason();
    }
}
