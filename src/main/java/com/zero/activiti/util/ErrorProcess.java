package com.zero.activiti.util;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Map;

/**
 * @author yezhaoxing
 * @since 2018/08/02
 */
@Slf4j
public class ErrorProcess implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        for (String s : variables.keySet()) {
            log.info("{}:{}", s, variables.get(s));
        }
    }
}
