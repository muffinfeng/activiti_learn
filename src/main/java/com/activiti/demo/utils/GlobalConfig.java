package com.activiti.demo.utils;

public class GlobalConfig {

    public static boolean TEST = true;

    public enum ResponseCode{
        SUCCESS(0,"成功"),
        FAIL(1,"失败");

        private final int code;
        private final String desc;
        ResponseCode(int code, String desc){
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
