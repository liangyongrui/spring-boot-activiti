package com.zero.activiti.util;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yezhaoxing
 * @since 2018/08/02
 */
@Component
public class PrinterProcess implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(PrinterProcess.class);

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("实例id: {}", delegateExecution.getProcessInstanceId());
        log.info("定义id: {}", delegateExecution.getProcessDefinitionId());
        Map<String, Object> variables = delegateExecution.getVariables();
        for (String s : variables.keySet()) {
            log.info("{}:{}", s, variables.get(s));
        }
        Map<String, Object> params = new HashMap<>();
        params.put("username", "yzx");
        params.put("leaveDay", 11);
        params.put("date", new Date());
        delegateExecution.setVariables(params);
    }
}
