package com.alex.meican.vo;

import lombok.Data;

/**
 * @author sunhuadong
 * @date 2020/5/12 9:09 下午
 */
@Data
public class ResponseData<T> {

    private int code;
    private String message;
    private T data;

    public static <T> ResponseData<T> success(T data) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setCode(0);
        responseData.setMessage("SUCCESS");
        responseData.setData(data);
        return responseData;
    }

    public static ResponseData<?> error(String message) {
        ResponseData<?> responseData = new ResponseData<>();
        responseData.setCode(-1);
        responseData.setMessage(message);
        return responseData;
    }
}
