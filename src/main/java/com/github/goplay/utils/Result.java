package com.github.goplay.utils;

import com.github.goplay.utils.Data.ResultCode;

import java.util.HashMap;
import java.util.Map;


public class Result<T> {
    /*返回体*/
    private Boolean success;
    private Integer code;
    private String message;

    private Map<String ,Object> data = new HashMap<>();
    private Object oData;

    private Result(){}

    public static Result ok(){
        Result r = new Result();
        r.setSuccess(true);
        r.setCode(ResultCode.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    public static Result error(){
        Result r = new Result();
        r.setSuccess(false);
        r.setCode(ResultCode.ERROR);
        r.setMessage("失败");
        return r;
    }

    public static Result empty(){
        Result r = new Result();
        r.setSuccess(true);
        r.setCode(ResultCode.EMPTY);
        r.setMessage("查询为空");
        return r;
    }

    public static Result expired(){
        Result r = new Result();
        r.setSuccess(false);
        r.setCode(ResultCode.EXPIRED);
        r.setMessage("登录信息已过期");
        return r;
    }

    public static Result uploadError(){
        Result r = new Result();
        r.setSuccess(false);
        r.setCode(ResultCode.UPLOAD_ERROR);
        r.setMessage("上传失败");
        return r;
    }

    public Result success(Boolean success){
        this.setSuccess(success);
        return this;
    }

    public Result code(Integer code){
        this.setCode(code);
        return this;
    }

    public Result message(String message){
        this.setMessage(message);
        return this;
    }

    public Result data(String key, Object value){
        this.data.put(key,value);
        return this;
    }

    public Result data(Map<String, Object> map){
        this.setData(map);
        return this;
    }

    public Result oData(Object oData){
        this.setoData(oData);
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getoData() {
        return oData;
    }

    public void setoData(Object oData) {
        this.oData = oData;
    }
}

