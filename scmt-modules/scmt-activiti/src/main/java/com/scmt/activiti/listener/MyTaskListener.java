package com.scmt.activiti.listener;


import com.scmt.activiti.entity.business.Leave;
import com.scmt.activiti.service.business.LeaveService;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author Exrickx
 */
@Slf4j
public class MyTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {

        // 获取关联业务表ID变量(启动流程代码里已存入tableId，此处直接获取即可)
        String tableId = (String) delegateTask.getVariable("tableId");
        log.info(tableId);
        LeaveService leaveService = SpringUtil.getBean(LeaveService.class);
        Leave leave = leaveService.get(tableId);

        // 获取其他流程信息
        String procInsId = delegateTask.getProcessInstanceId();
        log.info(procInsId);

        // 监听器中设置流程变量示例
        // delegateTask.setVariable("Scmt", "Scmt");
    }
}
