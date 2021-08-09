package com.activiti.demo.controller;


import com.activiti.demo.SecurityUtil;
import com.activiti.demo.utils.AjaxResponse;
import com.activiti.demo.utils.GlobalConfig;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/activitiHistory")
public class ActivitiHistoryController {

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    //用户历史
    @GetMapping(value = "/getInstancesByUserName")
    public AjaxResponse InstancesByUser() {
        try {

            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .orderByHistoricTaskInstanceEndTime().asc()
                    .taskAssignee("bajie")
                    .list();

            return AjaxResponse.ajaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), historicTaskInstances);
        } catch (Exception e) {
            return AjaxResponse.ajaxData(GlobalConfig.ResponseCode.FAIL.getCode(),
                    "获取历史任务失败", e.toString());
        }

    }

    //任务实例历史
    @GetMapping(value = "/getInstancesByPiID")
    public AjaxResponse getInstancesByPiID(@RequestParam("piID") String piID) {
        try {

            //--------------------------------------------另一种写法-------------------------
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .orderByHistoricTaskInstanceEndTime().asc()
                    .processInstanceId(piID)
                    .list();

            return AjaxResponse.ajaxData(GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(), historicTaskInstances);
        } catch (Exception e) {
            return AjaxResponse.ajaxData(GlobalConfig.ResponseCode.FAIL.getCode(),
                    "获取历史任务失败", e.toString());
        }

    }




}
