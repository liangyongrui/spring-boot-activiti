package com.zero.activiti;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yezhaoxing
 * @since 2018/08/02
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class ActivitiTest {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private FormService formService;

    @Test
    public void testCreateProcess() {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        Deployment deploy = deploymentBuilder.addClasspathResource("process/vocationRequest.bpmn20.xml").deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploy.getId()).singleResult();
        log.info("流程定义文件 [{}]，流程ID [{}]", processDefinition.getName(), processDefinition.getId());
    }

    @Test
    public void testStartProcess() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employeeName", "Zero");
        variables.put("numberOfDays", 4);
        variables.put("startDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        variables.put("vacationMotivation", "I'm really tired!");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("vacationRequest", variables);
        log.info("id of processInstance: " + processInstance.getId());
    }

    @Test
    public void testQueryManager() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            log.info("Task available: " + task.getName());
            Map<String, Object> taskVariables = new HashMap<>();
            taskVariables.put("vacationApproved", "false");
            taskVariables.put("managerMotivation", "We have a tight deadline!");
            taskService.complete(task.getId(), taskVariables);
        }
    }

    @Test
    public void testQueryEmployee() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateOrAssigned("Zero").list();
        for (Task task : tasks) {
            log.info("Task available: " + task.getName());
            Map<String, Object> taskVariables = new HashMap<>();
            taskVariables.put("resendRequest", "false");
            taskService.complete(task.getId(), taskVariables);
        }
    }

    @Test
    public void test() {
        StartFormData startFormData = formService.getStartFormData("vacationRequest:1:3");
        TaskFormData taskFormData = formService.getTaskFormData("10009");
    }
}
