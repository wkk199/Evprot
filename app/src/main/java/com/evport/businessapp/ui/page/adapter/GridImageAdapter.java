package com.evport.businessapp.ui.page.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evport.businessapp.R;
import com.evport.businessapp.ui.page.viewholder.ListGridImageViewHolder;

import java.util.List;

public class GridImageAdapter extends RecyclerView.Adapter<ListGridImageViewHolder> {
    Context context;
    List<String> data;
    onCallBack callBack;
    public GridImageAdapter(Context context, List<String> data){
        this.context=context;
        this.data=data;

    }
    @NonNull
    @Override
    public ListGridImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.adapter_img_grid,parent,false);
        return new ListGridImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListGridImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.e("hm---",data.get(position));
        Glide.with(context).load(data.get(position)).placeholder(R.drawable.img_error).error(R.drawable.img_error).into(holder.img_uri);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("hm---onClick","onClick");
                callBack.details(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    //步骤1:定义一个回调接口
    public interface onCallBack {

        public void details(int position);
    }

    //步骤2:设置监听器
    public void setOnCallBack(onCallBack callBack) {
        this.callBack = callBack;
    }
}
