package com.example.seckilldemo.util;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@ToString()
public class AjaxResult<T> implements Serializable {

    private int code;
    @JSONField(serialize = true)
    private String message;
    private String state;
    private T data;


    public AjaxResult() {
    }

    public AjaxResult(T data) {
        this.code = 200;
        this.data = data;
    }

    public AjaxResult(String state, T data) {
        this.code = 200;
        this.data = data;
        this.state = state;
    }

    public AjaxResult(int code, String state, T data) {
        this.code = code;
        this.data = data;
        this.state = state;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("state", getState());
        map.put("code", String.valueOf(getCode()));
        map.put("content", String.valueOf(getMessage()));
        return map;
    }

    public static AjaxResult success() {
        return success("操作成功");
    }

    public static AjaxResult login() {
        AjaxResult<Object> ajaxResult = new AjaxResult<>();
        ajaxResult.setState("loginOut");
        return ajaxResult;
    }

    public static AjaxResult success(String message) {
        AjaxResult<Object> ajaxResult = new AjaxResult<>();
        ajaxResult.setState("success");
        ajaxResult.setMessage(message);
        return ajaxResult;
    }
    public static AjaxResult data(Object data) {
        AjaxResult<Object> ajaxResult = new AjaxResult<>();
        ajaxResult.setState("success");
        ajaxResult.setMessage("成功");
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult success(String message, Object o) {
        AjaxResult<Object> ajaxResult = new AjaxResult<>();
        ajaxResult.setState("success");
        ajaxResult.setData(o);
        ajaxResult.setMessage(message);
        return ajaxResult;
    }

    public static AjaxResult error() {
        return error("操作失败");
    }

    public static AjaxResult error(String message) {
        AjaxResult<Object> ajaxResult = new AjaxResult<>();
        ajaxResult.setState("error");
        ajaxResult.setMessage(message);
        return ajaxResult;
    }

    public boolean isSuccess() {
        if ("success".equals(getState())) {
            return true;
        }
        return false;
    }


    public static AjaxResult result(Boolean result) {
        if (null == result || !result) {
            return AjaxResult.error();
        }
        return AjaxResult.success();
    }
}