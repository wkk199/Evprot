package com.evport.businessapp.data.bean;

import java.util.List;

public class FeedBackDataBean {
   List<Feedback> feedbackData ;
   int length;

    public List<Feedback> getFeedbackData() {
        return feedbackData;
    }

    public void setFeedbackData(List<Feedback> feedbackData) {
        this.feedbackData = feedbackData;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
