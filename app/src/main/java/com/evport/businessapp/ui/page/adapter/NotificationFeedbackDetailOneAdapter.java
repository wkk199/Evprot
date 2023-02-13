package com.evport.businessapp.ui.page.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evport.businessapp.R;
import com.evport.businessapp.ui.page.viewholder.NotificationFeedbackDetailOneViewHolder;

public class NotificationFeedbackDetailOneAdapter extends RecyclerView.Adapter<NotificationFeedbackDetailOneViewHolder> {
    Context context;
    public   NotificationFeedbackDetailOneAdapter( Context context){
        this.context=context;
    }
    @NonNull
    @Override
    public NotificationFeedbackDetailOneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notification_feedback_detail_item,null);
        return new NotificationFeedbackDetailOneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationFeedbackDetailOneViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
