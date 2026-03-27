package com.atatame.medicineassistantsystem.exception;


public interface ExceptionMessage {
    String SUCCESS= "ok";
    String PARAMS_ERROR="请求参数错误";
    String NOT_LOGIN_ERROR="未登录";
    String NO_AUTH_ERROR="无权限";
    String TOO_MANY_REQUEST= "请求过于频繁";
    String NOT_FOUND_ERROR="请求数据不存在";
    String FORBIDDEN_ERROR="禁止访问";
    String SYSTEM_ERROR= "系统内部异常";
    String OPERATION_ERROR= "操作失败";
    String ARG_NULL= "参数为空";

    String USER_NOT_FOUND="用户不存在";




}
