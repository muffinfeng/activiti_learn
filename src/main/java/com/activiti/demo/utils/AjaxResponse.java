package com.activiti.demo.utils;

public class AjaxResponse {
    private int status;
    private String msg;
    private Object obj;

    private AjaxResponse(int status, String msg, Object obj) {
        this.status = status;
        this.msg = msg;
        this.obj = obj;
    }

    public static AjaxResponse ajaxData(int status, String msg, Object obj){
        return new AjaxResponse(status,msg,obj);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }


}
