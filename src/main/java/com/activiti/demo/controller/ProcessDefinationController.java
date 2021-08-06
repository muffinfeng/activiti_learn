package com.activiti.demo.controller;

import com.activiti.demo.utils.AjaxResponse;
import com.activiti.demo.utils.GlobalConfig;
import jdk.internal.util.xml.impl.Input;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping(value = "processDefination")
public class ProcessDefinationController {

    @Autowired
    private RepositoryService repositoryService;

    //流程定义列表
    @GetMapping(value = "getDefinations")
    public AjaxResponse getDefinitions(){
        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>() ;

        try{
            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();

            for(ProcessDefinition pd : list){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("Name", pd.getName());
                map.put("Key", pd.getKey());
                map.put("ResourceName", pd.getResourceName());
                map.put("DeploymentId", pd.getDeploymentId());
                map.put("Version", pd.getVersion());

                resultList.add(map);
            }

        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "获取流程定义失败",
                    e.getMessage()
            );
        }

        return AjaxResponse.ajaxData(
                GlobalConfig.ResponseCode.SUCCESS.getCode(),
                GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                resultList
        );
    }

    //上传BPMN流媒体
    @PostMapping(value = "uploadStreamAndDeployment")
    public AjaxResponse uploadStreamAndDeployment(@RequestParam("processFile")MultipartFile processFile,
                                                  @RequestParam("deploymentName")String deploymentName){

        try{
            //获取文件名
            String fileName = processFile.getOriginalFilename();
            //获取文件名后缀
            String extension = FilenameUtils.getExtension(fileName);
            //获取文件流
            InputStream inputStream = processFile.getInputStream();

            Deployment deployment = null;
            if("zip".equals(extension)){
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                deployment = repositoryService.createDeployment()
                        .addZipInputStream(zipInputStream)
                        .name(deploymentName)
                        .deploy();

            }else{
                deployment = repositoryService.createDeployment()
                        .addInputStream(fileName, inputStream)
                        .name(deploymentName)
                        .deploy();
            }

            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                    deployment.getId()+ " ; " + deployment.getName()
                    );
        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "上传BPMN流媒体失败",
                    e.getMessage()
            );
        }

    }

    //上传BPMN的xml string
    @PostMapping(value = "addDeploymentByString")
    public AjaxResponse addDeploymentByString(@RequestParam("stringBPMN")String stringBPMN,
                                                  @RequestParam("deploymentName")String deploymentName){
        try{
            Deployment deployment = null;
            deployment = repositoryService.createDeployment()
                    .addString("CreateWithBPMNJS.bpmn",stringBPMN)
                    .name(deploymentName)
                    .deploy();

            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                    deployment.getId()
            );
        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "上传上传BPMN的xml string失败",
                    e.getMessage()
            );
        }

    }


    //获取流程定义XML
    @GetMapping(value = "getDefinationXML")
    public void getDefinationXML(@RequestParam("deploymentID")String deploymentID,
                                              @RequestParam("resourceName")String resourceName,
                                              HttpServletResponse response){
        try{
            InputStream inputStream = repositoryService.getResourceAsStream(deploymentID, resourceName);
            int count = inputStream.available();
            byte[] bytes = new byte[count];

            response.setContentType("text/xml");
            OutputStream outputStream = response.getOutputStream();
            if(inputStream.read(bytes) != 0){
                outputStream.write(bytes);
            }
            inputStream.close();

        }catch (Exception e){

        }

    }


    //获取流程部署列表
    @GetMapping(value = "getDeployments")
    public AjaxResponse getDeployments(){

        List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>() ;

        try{

            List<Deployment> list = repositoryService.createDeploymentQuery().list();
            for(Deployment dep : list){
                HashMap<String, Object> map = new HashMap<>();

                map.put("id", dep.getId());
                map.put("Name", dep.getName());
                map.put("DeploymentTime", dep.getDeploymentTime());
                map.put("Key", dep.getKey());

                resultList.add(map);
            }

            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                    resultList
            );


        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "获取流程部署列表失败",
                    e.getMessage()
            );
        }


    }


    //删除流程定义
    @GetMapping(value = "delDefination")
    public AjaxResponse delDefination(@RequestParam("pdID") String pdID){


        try{

            repositoryService.deleteDeployment(pdID, true);

            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.SUCCESS.getCode(),
                    GlobalConfig.ResponseCode.SUCCESS.getDesc(),
                    null
            );


        }catch (Exception e){
            return AjaxResponse.ajaxData(
                    GlobalConfig.ResponseCode.FAIL.getCode(),
                    "删除流程定义失败",
                    e.getMessage()
            );
        }


    }
}
