package com.zero.activiti.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author yezhaoxing
 * @since 2018/07/31
 */
@RestController
@RequestMapping("/process")
@Slf4j
@Api(description = "工作流控制器")
public class ActivitiController {

    @Resource
    private ProcessEngine processEngine;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private FormService formService;
    @Resource
    private HistoryService historyService;

    @PostMapping("/create")
    @ApiOperation("新建流程")
    public String createProcess(MultipartFile xmlFile, String xmlName, MultipartFile pngFile, String pngName)
            throws IOException {

        Deployment deployment = repositoryService
                .createDeployment()
                .addInputStream(xmlName, xmlFile.getInputStream())
                .addInputStream(pngName, pngFile.getInputStream())
                .deploy();

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        log.info("流程定义文件 [{}]，流程ID [{}]", processDefinition.getName(), processDefinition.getId());
        return processDefinition.getId();
    }

    @PostMapping("/start")
    @ApiOperation("启动流程")
    public String startProcess(String processDefinitionId) {

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);
        log.info("启动流程 [{}]", processInstance.getProcessDefinitionKey());
        return processInstance.getId();
    }

    @PostMapping("/list")
    @ApiOperation("查询所有流程任务")
    public Map<String, Object> listProcess(String processInstanceId) {

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        Map<String, Object> rtn = new HashMap<>();

        if (processInstance != null && !processInstance.isEnded()) {
            Task task = taskService
                    .createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            TaskFormData taskFormData = formService.getTaskFormData(task.getId());

            rtn.put("ID", task.getId());
            rtn.put("NAME", task.getName());
            rtn.put("TASK_NAME", 1);
            rtn.put("FORM_DATA", taskFormData.getFormProperties());
        } else {
            rtn.put("NAME", "END");
        }
        return rtn;
    }

    @PostMapping("/handler")
    @ApiOperation("处理流程任务")
    public String handlerProcessTask(String taskId, @RequestBody Map<String, Object> taskParam) {

        log.info("待处理任务 [{}]", taskId);
        taskService.complete(taskId, taskParam);
        return "success";
    }

    @GetMapping(value = "/writePic")
    @ApiOperation("获取引擎流程图片")
    @SuppressWarnings("unchecked")
    public void readResource(@RequestParam String processInstanceId) {
        // 获得流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        String processDefinitionId = "";
        if (processInstance == null) {
            // 查询已经结束的流程实例
            HistoricProcessInstance processInstanceHistory = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId).singleResult();
            if (processInstanceHistory != null) {
                processDefinitionId = processInstanceHistory.getProcessDefinitionId();
            }
        } else {
            processDefinitionId = processInstance.getProcessDefinitionId();
        }

        // 使用宋体
        String fontName = "宋体";
        // 获取BPMN模型对象
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        // 获取流程实例当前的节点，需要高亮显示
        List<String> currentActs = (List<String>)Collections.EMPTY_LIST;
        if (processInstance != null)
            currentActs = runtimeService.getActiveActivityIds(processInstance.getId());

        InputStream inputStream = processEngine
                .getProcessEngineConfiguration()
                .getProcessDiagramGenerator()
                .generateDiagram(model, "png", currentActs, new ArrayList<>(), fontName, fontName, fontName, null, 1.0);
    }
}
