package com.scmt.activiti.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.activiti.engine.task.Task;

import java.util.Date;

/**
 * @author Exrickx
 */
@Data
@Accessors(chain = true)
public class TaskVo {

    private String id;

    private String name;

    private String key;

    private String description;

    private String executionId;

    private String assignee;

    private String owner;

    private String ownerUsername;

    private String procDefId;

    private String procInstId;

    private String applyer;

    private String applyerUsername;

    private String category;

    private Integer priority;

    private Boolean isSuspended;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private String processName;

    private String routeName;

    private String businessKey;

    private String tableId;

    private Integer version;

    public TaskVo(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.key = task.getTaskDefinitionKey();
        this.description = task.getDescription();
        this.executionId = task.getExecutionId();
        this.assignee = task.getAssignee();
        this.owner = task.getOwner();
        this.procDefId = task.getProcessDefinitionId();
        this.procInstId = task.getProcessInstanceId();
        this.priority = task.getPriority();
        this.isSuspended = task.isSuspended();
        this.category = task.getCategory();
        this.createTime = task.getCreateTime();
    }
}
