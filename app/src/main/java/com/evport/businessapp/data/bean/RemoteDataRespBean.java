package com.evport.businessapp.data.bean;

import androidx.annotation.Keep;

@Keep
public class RemoteDataRespBean {

    String code;
    String message;
    String success;
    RemoteDataBean data;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public RemoteDataBean getData() {
        return data;
    }

    public void setData(RemoteDataBean data) {
        this.data = data;
    }
}


