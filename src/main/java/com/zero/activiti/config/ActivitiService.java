package com.zero.activiti.config;

import org.activiti.engine.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;

/**
 * @author yezhaoxing
 * @since 2018/08/01
 */
@Configuration
public class ActivitiService {

    @Resource
    private ProcessEngine processEngine;

    @Bean
    @DependsOn("processEngine")
    public RuntimeService runtimeService() {
        return processEngine.getRuntimeService();
    }

    @Bean
    @DependsOn("processEngine")
    public RepositoryService repositoryService() {
        return processEngine.getRepositoryService();
    }

    @Bean
    @DependsOn("processEngine")
    public TaskService taskService() {
        return processEngine.getTaskService();
    }

    @Bean
    @DependsOn("processEngine")
    public FormService formService() {
        return processEngine.getFormService();
    }

    @Bean
    @DependsOn("processEngine")
    public HistoryService historyService() {
        return processEngine.getHistoryService();
    }
}
