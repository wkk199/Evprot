package com.evport.businessapp.data.bean;

public class DataBean {
    private boolean isNewRecord;

    private String userName;

    private String  feedbackPk;

    private String feedbackDatetime;

    private String email;

    private String userPk;

    private String feedbackContent;

    private String feedbackTag;

    private String imgDir;

    public void setIsNewRecord(boolean isNewRecord){
        this.isNewRecord = isNewRecord;
    }
    public boolean getIsNewRecord(){
        return this.isNewRecord;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return this.userName;
    }
    public void setFeedbackPk(String feedbackPk){
        this.feedbackPk = feedbackPk;
    }
    public String getFeedbackPk(){
        return this.feedbackPk;
    }
    public void setFeedbackDatetime(String feedbackDatetime){
        this.feedbackDatetime = feedbackDatetime;
    }
    public String getFeedbackDatetime(){
        return this.feedbackDatetime;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setUserPk(String userPk){
        this.userPk = userPk;
    }
    public String getUserPk(){
        return this.userPk;
    }
    public void setFeedbackContent(String feedbackContent){
        this.feedbackContent = feedbackContent;
    }
    public String getFeedbackContent(){
        return this.feedbackContent;
    }
    public void setFeedbackTag(String feedbackTag){
        this.feedbackTag = feedbackTag;
    }
    public String getFeedbackTag(){
        return this.feedbackTag;
    }
    public void setImgDir(String imgDir){
        this.imgDir = imgDir;
    }
    public String getImgDir(){
        return this.imgDir;
    }

}
