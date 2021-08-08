package com.activiti.demo.controller;

import com.activiti.demo.SecurityUtil;
import com.activiti.demo.pojo.UserInfoBean;
import com.activiti.demo.utils.AjaxResponse;
import com.activiti.demo.utils.GlobalConfig;
import org.activiti.api.model.shared.model.VariableInstance;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "processInstance")
public class ProcessInstanceController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private ProcessRuntime processRuntime;

    //获取流程实例
    @GetMapping(value = "getInstances")
    public AjaxResponse getInstances(@AuthenticationPrincipal UserInfoBean userInfoBean){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }
        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>() ;

        try {
            Page<ProcessInstance> processInstancs = processRuntime.processInstances(Pageable.of(0, 100));
            List<ProcessInstance> list = processInstancs.getContent();
            list.sort((y,x)-> x.getStartDate().toString().compareTo(y.getStartDate().toString()));

            for(ProcessInstance pi : list){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("id", pi.getId());
                map.put("name", pi.getName());
                map.put("status", pi.getStatus());
                map.put("processDefinationId", pi.getProcessDefinitionId());
                map.put("processDefinationKey", pi.getProcessDefinitionKey());
                map.put("startDate",pi.getStartDate());
                map.put("businessKey",pi.getBusinessKey());

                ProcessDefinition processDefinition =  repositoryService.createProcessDefinitionQuery()
                        .processDefinitionId(pi.getProcessDefinitionId())
                        .singleResult();

                map.put("resourceName",processDefinition.getResourceName());
                map.put("deploymentID",processDefinition.getDeploymentId());

                resultList.add(map);
            }
        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "获取流程实例失败",
                    e.getMessage()
            );
        }

        return AjaxResponse.ajaxData(
                GlobalConfig.ResponseCode.SUCCESS.getCode(),
                GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                resultList
        );
    }

    //开启流程实例
    @GetMapping(value = "startProcess")
    public AjaxResponse startProcess(@RequestParam("processDefinationKey") String processDefinationKey,
                                        @RequestParam("instanceName") String instanceName){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }

        try {
            processRuntime.start(ProcessPayloadBuilder.start()
                    .withProcessDefinitionKey(processDefinationKey)
                    .withName(instanceName)
                    .withBusinessKey("我是busness_key")
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
                    "开启流程实例失败",
                    e.getMessage()
            );
        }
    }

    //挂起流程实例
    @GetMapping(value = "suspendProcess")
    public AjaxResponse suspendProcess(@RequestParam("instanceID") String instanceID){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }

        try {
            processRuntime.suspend(ProcessPayloadBuilder
                    .suspend()
                    .withProcessInstanceId(instanceID)
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
                    "挂起流程实例失败",
                    e.getMessage()
            );
        }
    }

    //恢复流程实例
    @GetMapping(value = "resumeProcess")
    public AjaxResponse resumeProcess(@RequestParam("instanceID") String instanceID){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }

        try {
            processRuntime.resume(ProcessPayloadBuilder
                    .resume()
                    .withProcessInstanceId(instanceID)
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
                    "恢复流程实例失败",
                    e.getMessage()
            );
        }
    }

    //删除流程实例
    @GetMapping(value = "deleteInstance")
    public AjaxResponse deleteInstance(@RequestParam("instanceID") String instanceID){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }

        try {
            processRuntime.delete(ProcessPayloadBuilder
                    .delete()
                    .withProcessInstanceId(instanceID)
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
                    "删除流程实例失败",
                    e.getMessage()
            );
        }
    }

    //获取流程实例参数
    @GetMapping(value = "variables")
    public AjaxResponse variables(@RequestParam("instanceID") String instanceID){
        if(GlobalConfig.TEST){
            securityUtil.logInAs("bajie");
        }

        try {
            List<VariableInstance> variableInstances = processRuntime.variables(ProcessPayloadBuilder
                    .variables()
                    .withProcessInstanceId(instanceID)
                    .build()
            );

            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                    variableInstances
            );
        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "获取流程实例参数失败",
                    e.getMessage()
            );
        }
    }

}
