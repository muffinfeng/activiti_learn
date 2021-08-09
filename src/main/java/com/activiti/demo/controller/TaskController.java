package com.activiti.demo.controller;

import com.activiti.demo.SecurityUtil;
import com.activiti.demo.pojo.UserInfoBean;
import com.activiti.demo.utils.AjaxResponse;
import com.activiti.demo.utils.GlobalConfig;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.ClaimTaskPayloadBuilder;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.model.payloads.ClaimTaskPayload;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class TaskController {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private RepositoryService repositoryService;

    //获取我的待办任务
    @GetMapping(value = "getTasks")
    public AjaxResponse getTasks(){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }
        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>() ;

        try {
            Page<Task> page = taskRuntime.tasks(Pageable.of(0,100));
            List<Task> list = page.getContent();

            for(Task task : list){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("id", task.getId());
                map.put("name", task.getName());
                map.put("status", task.getStatus());
                map.put("createdDate", task.getCreatedDate());

                if(task.getAssignee() == null){
                    map.put("assignee", "我是候选人");
                }
                map.put("assignee", task.getAssignee());

                ProcessInstance processInstance = processRuntime.processInstance(task.getProcessDefinitionId());
                map.put("processInstanceName", processInstance.getName());

                resultList.add(map);
            }
        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "获取我的待办任务失败",
                    e.getMessage()
            );
        }

        return AjaxResponse.ajaxData(
                GlobalConfig.ResponseCode.SUCCESS.getCode(),
                GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                resultList
        );
    }

    //完成任务
    @GetMapping(value = "completeTask")
    public AjaxResponse completeTask(@RequestParam("taskID") String taskID){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }

        try {
            Task task = taskRuntime.task(taskID);
            if(task.getAssignee() == null){
                taskRuntime.claim(TaskPayloadBuilder
                        .claim()
                        .withTaskId(taskID)
                        .build());
            }

            taskRuntime.complete(TaskPayloadBuilder
                        .complete()
                        .withTaskId(taskID)
//                        .withVariable("num","2")
                        .build()
            );

            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                    null
            );
        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "完成任务失败",
                    e.getMessage()
            );
        }
    }

    //动态渲染表单
    @GetMapping(value = "formDataShow")
    public AjaxResponse formDataShow(@RequestParam("taskID") String taskID){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }
        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>() ;

        try {
            Task task = taskRuntime.task(taskID);
            UserTask userTask = (UserTask)repositoryService.getBpmnModel(task.getProcessDefinitionId())
                    .getFlowElement(task.getFormKey());

            if(userTask == null){
                return AjaxResponse.ajaxData(
                        GlobalConfig.ResponseCode.SUCCESS.getCode(),
                        GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                        null
                );
            }

            List<FormProperty> list = userTask.getFormProperties();
            for(FormProperty fp : list){
                HashMap<String, Object> map = new HashMap<String, Object>();
                String[] props = fp.getId().split("-_!");
                map.put("id",props[0]);
                map.put("controlType",props[1]);
                map.put("controlLable",props[2]);
                map.put("controlDefvalue",props[3]);
                map.put("controlParam",props[4]);

                resultList.add(map);
            }



        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "动态渲染表单失败",
                    e.getMessage()
            );
        }

        return AjaxResponse.ajaxData(
                GlobalConfig.ResponseCode.SUCCESS.getCode(),
                GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                resultList
        );
    }

    //动态表单提交
    @PostMapping (value = "formDataSave")
    public AjaxResponse formDataSave(@RequestParam("taskID") String taskID,
                                     @RequestParam("formDataString") String formDataString){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }
        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>() ;

        try {
            Task task = taskRuntime.task(taskID);

            String[] formDatas = formDataString.split("!_!");
            for(String formData : formDatas){
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                String[] props = formData.split("-_!");

                hashMap.put("PROC_DEF_ID_", task.getProcessDefinitionId());
                hashMap.put("PROC_INST_ID_", task.getProcessInstanceId());
                hashMap.put("FORM_KEY_", task.getFormKey());
                hashMap.put("Control_ID_", props[0]);
                hashMap.put("Control_VALUE_", props[1]);
                hashMap.put("Control_PARAM_", props[2]);

                resultList.add(hashMap);
            }



        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "动态渲染表单失败",
                    e.getMessage()
            );
        }

        return AjaxResponse.ajaxData(
                GlobalConfig.ResponseCode.SUCCESS.getCode(),
                GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                resultList
        );
    }
}
