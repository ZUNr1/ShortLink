package org.zunr1.backend.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "success";
        result.data = data;
        return result;
    }


    public static <T> Result<T> error(Integer code,String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public boolean isSuccess(){
        return code == 200;
    }
}
