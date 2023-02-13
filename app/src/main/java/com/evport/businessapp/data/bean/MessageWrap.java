package com.evport.businessapp.data.bean;

public class MessageWrap {
    int type;
     String data;
     public MessageWrap(int type, String data){
         this.data=data;
         this.type=type;
     }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
