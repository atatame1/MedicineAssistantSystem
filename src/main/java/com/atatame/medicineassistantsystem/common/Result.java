package com.atatame.medicineassistantsystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Boolean success;
    private String message;
    private T data;

    public static Result<Void> ok(){
        return new Result<>(true, null, null);
    }
    public static<T> Result<T> ok(T data){
        return new Result<>(true, null, data);
    }
    public static<T> Result<List<T>> ok(List<T> data, Long total){
        return new Result<>(true, null, data);
    }
    public static Result<Void> fail(String errorMsg){
        return new Result<>(false, errorMsg, null);
    }

    public static Result<Void> fail(){
        return new Result<>(false, "系统错误", null);
    }

}
